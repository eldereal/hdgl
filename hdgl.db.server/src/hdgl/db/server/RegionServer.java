package hdgl.db.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.ipc.RPC;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import hdgl.db.conf.GraphConf;
import hdgl.db.conf.MasterConf;
import hdgl.db.exception.HdglException;
import hdgl.db.protocol.ClientRegionProtocol;
import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.db.protocol.RegionMasterProtocol;
import hdgl.db.store.Log;
import hdgl.db.store.LogStore;
import hdgl.db.store.StoreFactory;
import hdgl.util.StringHelper;
import hdgl.util.WritableHelper;


public class RegionServer implements ClientRegionProtocol, Watcher {

	static final Pattern ZK_ID_PATTERN = Pattern.compile(".*?(\\d+)");
	
	Configuration conf;
	RegionMasterProtocol master;
	String host;
	int port;
	int regionId;
	ZooKeeper zk; 
	FileSystem fs;
	
	Map<Integer, LogStore> logStores = new ConcurrentHashMap<Integer, LogStore>();
	
	Map<Integer, Boolean> taskResults =  new ConcurrentHashMap<Integer, Boolean>();
	
	public ZooKeeper zk() throws IOException, InterruptedException, KeeperException{
		if(this.zk == null){
			this.zk = HConf.getZooKeeper(conf, this);
		}
		if(zk.exists(GraphConf.getZookeeperRoot(conf), false)==null){
			zk.create(GraphConf.getZookeeperRoot(conf), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		return zk;
	}
	
	public FileSystem fs() throws IOException{
		if(this.fs == null){
			this.fs = HConf.getFileSystem(conf);
		}
		return fs;
	}
	
	public RegionServer(String host, int port, Configuration conf){
		this.host = host;
		this.port = port;
		this.conf = conf;
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
		master.regionStop();
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
		master.regionStart();
	}

	@Override
	public InetSocketAddressWritable[] doQuery(int queryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[][] fetchResult(int queryId) {
		// TODO Auto-generated method stub
		return null;
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
		try {
			LogStore store = logStores.get(txId);
			if(store==null){
				throw new HdglException("Bad Transaction Id");
			}
			FileStatus logfile = store.close();
			taskResults.put(txId, false);
			logStores.remove(txId);
			return txId;
		}catch (Exception e) {
			throw new HdglException(e);
		}
	}

	@Override
	public int abort(int txId) {
		try {
			LogStore store = logStores.get(txId);
			if(store==null){
				throw new HdglException("Bad Transaction Id");
			}
			FileStatus logfile = store.close();
			fs.delete(logfile.getPath(), false);
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
	
}
