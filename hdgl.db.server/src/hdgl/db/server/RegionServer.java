package hdgl.db.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.ipc.RPC;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import hdgl.db.conf.GraphConf;
import hdgl.db.conf.MasterConf;
import hdgl.db.exception.HdglException;
import hdgl.db.protocol.MessagePackWritable;
import hdgl.db.protocol.RegionProtocol;
import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.db.protocol.MessageWritable;
import hdgl.db.protocol.RegionMasterProtocol;
import hdgl.db.query.QueryContext;
import hdgl.db.server.bsp.BSPContainer;
import hdgl.db.server.bsp.BSPRunner;
import hdgl.db.store.GraphStore;
import hdgl.db.store.Log;
import hdgl.db.store.LogStore;
import hdgl.db.store.StoreFactory;
import hdgl.util.IterableHelper;
import hdgl.util.NetHelper;
import hdgl.util.StringHelper;
import hdgl.util.WritableHelper;


public class RegionServer implements RegionProtocol, Watcher, BSPContainer {

	static final Pattern ZK_ID_PATTERN = Pattern.compile(".*?(\\d+)");
	
	private static final org.apache.commons.logging.Log log = LogFactory.getLog(HGRegion.class);
	
	Configuration conf;
	RegionMasterProtocol master;
	String host;
	int port;
	int regionId;
	ZooKeeper zk; 
	GraphStore graph;
	
	Map<Integer, LogStore> logStores = new ConcurrentHashMap<Integer, LogStore>();
	
	Map<Integer, Boolean> taskResults =  new ConcurrentHashMap<Integer, Boolean>();
	
	Map<Integer, InetSocketAddressWritable> regions = new HashMap<Integer, InetSocketAddressWritable>();
	Map<Integer, RegionProtocol> regionConns = new HashMap<Integer, RegionProtocol>();
	Map<Integer, QueryContext> queries = new HashMap<Integer, QueryContext>();
	Map<Integer, BSPRunner> bspRunners = new HashMap<Integer, BSPRunner>();
	
	public ZooKeeper zk() throws IOException, InterruptedException, KeeperException{
		if(this.zk == null){
			this.zk = HConf.getZooKeeper(conf, this);
		}
		if(zk.exists(GraphConf.getZookeeperRoot(conf), false)==null){
			zk.create(GraphConf.getZookeeperRoot(conf), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		return zk;
	}
	
	public RegionServer(String host, int port, Configuration conf){
		this.host = host;
		this.port = port;
		this.conf = conf;
	}

	void updateRegions(){
		try{
			List<String> paths = zk().getChildren(HConf.getZKRegionRoot(conf), true);
			regions.clear();
			regionConns.clear();
			for (int i = 0; i < paths.size(); i++) {
				String path = StringHelper.makePath(HConf.getZKRegionRoot(conf), paths.get(i));
				Stat s = zk().exists(path, false);
				byte[] addrData = zk().getData(path, false, s);
				int regionId = StringHelper.getLastInt(path);
				InetSocketAddressWritable addr=WritableHelper.parse(addrData, InetSocketAddressWritable.class);
				regions.put(regionId, addr);
				regionConns.put(regionId, RPC.getProxy(RegionProtocol.class, 1, addr.toAddress(), conf));
			}
		}catch(Exception ex){
			log.error(ex);
		}
	}
	
	@Override
	public String echo(String value) {
		return value;
	}

	@Override
	public byte[] getEntity(long id) {
		return null;
	}
	
	public void stop(){
		//master.regionStop();
		try{
			zk.close();			
		}catch(Exception ex){
			
		}
		if(graph!=null){
			graph.close();
		}
		for(LogStore stores:logStores.values()){
			try{
				stores.close();
			}catch(Exception ex){
				
			}
		}
	}

	public void start() throws IOException, KeeperException, InterruptedException {
		final String mhost = MasterConf.getMasterHost(conf);
		final int mport = MasterConf.getRegionMasterPort(conf);
		master = RPC.getProxy(RegionMasterProtocol.class, 1, new InetSocketAddress(mhost, mport), conf);
		String zkRegionRoot = HConf.getZKRegionRoot(conf);
		if(zk().exists(zkRegionRoot, false)==null){
			zk().create(zkRegionRoot, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		InetSocketAddressWritable myAddress = new InetSocketAddressWritable(host, port);
		String path = zk().create(StringHelper.makePath(zkRegionRoot, "region"), WritableHelper.toBytes(myAddress), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		Matcher m = ZK_ID_PATTERN.matcher(path);
		if(!m.matches()){
			throw new IOException("Wrong path");
		}
		regionId = Integer.parseInt(m.group(1));
		graph=StoreFactory.createGraphStore(conf);
		//master.regionStart();
		updateRegions();
//		long step = graph.getVertexCountPerBlock();
//		long max = graph.getVertexCount();
//		String localhost = NetHelper.getMyHostName();
//		for(int id=0;id<max;id+=step){
//			String[] hosts = graph.bestPlacesForVertex(id);
//			for(String host:hosts){
//				if(host.equals(localhost)){
//					log.info("try starting bfs host id=(" + id + "-" + (id + step - 1) + ") on " + host);
//					try{
//						zk().create(StringHelper.makePath(HConf.getZKBSPRoot(conf), Long.toString(id/step)), WritableHelper.toBytes(myAddress), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//					}catch(NodeExistsException ex){
//						log.info("another bfs host already started id=(" + id + "-" + (id + step - 1) + ")");						
//					}
//					break;
//				}
//			}
//		}		
	}

	@Override
	public int doQuery(int queryId, int pathlen) {
		try{
			if(pathlen==0){
				List<String> ids = zk().getChildren(HConf.getZKQuerySessionRoot(conf), false);
				for(int i=ids.size()-1;i>=0;i--){
					String id=ids.get(i);
					if(StringHelper.getLastInt(id)==queryId){
						QueryContext ctx = WritableHelper.parse(
								zk().getData(StringHelper.makePath(HConf.getZKQuerySessionRoot(conf), id), false, null),
								QueryContext.class);
						queries.put(queryId, ctx);
						Set<Integer> regions = new HashSet<Integer>();
						for(Map.Entry<Long, Integer> map : ctx.getIdMap().entrySet()){
							regions.add(map.getValue());
						}
						int regionCount = regions.size();
						BSPRunner bspRunner = new BSPRunner(graph,ctx, ctx.getZkRoot(), regionCount, regionId, queryId, this, conf);
						bspRunners.put(queryId, bspRunner);
						bspRunner.start();
						return 1;
					}
				}
				throw new HdglException("Bad query id");
			}else{
				throw new HdglException("Unsupported");
			}
		}catch(IOException ex){
			throw new HdglException(ex);
		} catch (KeeperException ex) {
			throw new HdglException(ex);
		} catch (InterruptedException ex) {
			throw new HdglException(ex);
		}
	}

	@Override
	public long[][] fetchResult(int queryId, int pathLen) {
		try{
			BSPRunner runner=bspRunners.get(queryId);
			if(runner==null){
				throw new HdglException("Bad query id");
			}
			Object mutex = queries.get(queryId);
			while(runner.getSuperStep() < pathLen){
				synchronized(mutex){
					mutex.wait();
				}
			}
			return null;
		}catch(InterruptedException ex){
			throw new HdglException(ex);
		}
	}

	@Override
	public int beginTx() {
		try{
			String sessionRoot = HConf.getZKSessionRoot(conf);
			if(zk().exists(sessionRoot, false)==null){
				zk().create(sessionRoot, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			String path = zk().create(StringHelper.makePath(sessionRoot, "region"), null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			Matcher m = ZK_ID_PATTERN.matcher(path);
			if(!m.matches()){
				throw new IOException("Wrong path");
			}
			int sessionId = Integer.parseInt(m.group(1));
			logStores.put(sessionId, StoreFactory.createLogStore(conf, sessionId));
			return sessionId;
		}catch (Exception e) {
			throw new HdglException(e);
		}
	}

	@Override
	public void writeLog(int txId, Log log) {
		LogStore store = logStores.get(txId);
		if(store==null){
			throw new HdglException("Bad Transaction Id");
		}
		try {
			store.writeLog(log);
		} catch (IOException e) {
			throw new HdglException(e);
		}
	}

	@Override
	public int commit(int txId) {
		abort(txId);
		taskResults.put(txId, false);
		return txId;
	}

	@Override
	public int abort(int txId) {
		try {
			LogStore store = logStores.get(txId);
			if(store==null){
				throw new HdglException("Bad Transaction Id");
			}
			store.abort();
			store.close();
			logStores.remove(txId);
			taskResults.put(txId, true);
			return txId;
		}catch (Exception e) {
			throw new HdglException(e);
		}
	}

	@Override
	public boolean txTaskStatus(int txId, int waitMilliseconds) {
		if(taskResults.containsKey(txId)){
			return true;
		}else{
			try {
				taskResults.wait(waitMilliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(taskResults.containsKey(txId)){
				return true;
			}else{
				return false;
			}
		}		
	}

	@Override
	public boolean txTaskResult(int txId) {
		if(taskResults.containsKey(txId)){
			return taskResults.get(txId);
		}else{
			throw new HdglException("Task not complte yet");
		}
	}
	
	@Override
	public void process(WatchedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(int querySession, MessagePackWritable msg) {
		BSPRunner bspRunner = bspRunners.get(querySession);
		if(bspRunner==null){
			throw new HdglException("Bad query id");
		}
		bspRunner.receiveMessages(msg);		
	}

	@Override
	public void sendMessagePack(int querySession, int regionId, MessagePackWritable pack) {
		if(regionId==this.regionId||!regionConns.containsKey(regionId)){
			sendMessage(querySession, pack);
		}else{
			RegionProtocol connProtocol=regionConns.get(regionId);
			connProtocol.sendMessage(querySession, pack);
		}
	}

	@Override
	public void superStepFinish(int sessionId, int superstep) {
		Object mutex = queries.get(sessionId);
		synchronized(mutex){
			mutex.notifyAll();
		}
	}
	
}
