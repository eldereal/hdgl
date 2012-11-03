package hdgl.db.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;



import hdgl.db.conf.GraphConf;
import hdgl.db.exception.BadQueryException;
import hdgl.db.exception.HdglException;
import hdgl.db.impl.HGraphIds;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.db.protocol.RegionMasterProtocol;
import hdgl.db.query.convert.QueryCompletion;
import hdgl.db.query.convert.QueryToStateMachine;
import hdgl.db.query.expression.Expression;
import hdgl.db.query.parser.QueryLexer;
import hdgl.db.query.parser.QueryParser;
import hdgl.db.query.stm.SimpleStateMachine;
import hdgl.db.query.stm.StateMachine;
import hdgl.db.store.GraphStore;
import hdgl.db.store.StoreFactory;
import hdgl.util.StringHelper;
import hdgl.util.WritableHelper;


public class MasterServer implements RegionMasterProtocol, ClientMasterProtocol, Watcher {

	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(MasterServer.class);
	
	Configuration conf;
	
	String host;
	int port;
	ZooKeeper zk; 
	int masterId;
	GraphStore store;
	Map<Integer, InetSocketAddressWritable> regions = new HashMap<Integer, InetSocketAddressWritable>();
	
	public ZooKeeper zk() throws IOException, InterruptedException, KeeperException{
		if(this.zk == null){
			this.zk = HConf.getZooKeeper(conf, this);
		}
		if(zk.exists(GraphConf.getZookeeperRoot(conf), false)==null){
			zk.create(GraphConf.getZookeeperRoot(conf), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		return zk;
	}
	
	public MasterServer(String host, int port, Configuration conf){
		this.conf = conf;
		this.host = host;
		this.port = port;
	}

	public void start() throws IOException, KeeperException, InterruptedException{
		String masterZkNode;
		masterZkNode = HConf.getZKMasterRoot(conf);
		InetSocketAddressWritable myAddress = new InetSocketAddressWritable(host, port);
		if(zk().exists(masterZkNode, false)==null){
			zk().create(masterZkNode, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if(zk().exists(HConf.getZKQuerySessionRoot(conf), false)==null){
			zk().create(HConf.getZKQuerySessionRoot(conf), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		String path = zk().create(StringHelper.makePath(masterZkNode,"master"), WritableHelper.toBytes(myAddress), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		if(zk().exists(HConf.getZKRegionRoot(conf), false) == null){
			zk().create(HConf.getZKRegionRoot(conf), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk().getChildren(HConf.getZKRegionRoot(conf), this);
		}
		store = StoreFactory.createGraphStore(conf);
		masterId = StringHelper.getLastInt(path);
	}
	
	public void stop(){
		
	}

	void updateRegions(){
		try{
			List<String> paths = zk().getChildren(HConf.getZKRegionRoot(conf), false);
			regions.clear();
			for (int i = 0; i < paths.size(); i++) {
				String path = StringHelper.makePath(HConf.getZKRegionRoot(conf), paths.get(i));
				Stat s = zk().exists(path, false);
				byte[] addr = zk().getData(path, false, s);
				int regionId = StringHelper.getLastInt(path);
				regions.put(regionId, WritableHelper.parse(addr, InetSocketAddressWritable.class));
			}
		}catch(Exception ex){
			Log.error(ex);
		}
	}
	
	@Override
	public MapWritable getRegions() {
		MapWritable mapWritable = new MapWritable();
		for(Map.Entry<Integer, InetSocketAddressWritable> region:regions.entrySet()){
			mapWritable.put(new IntWritable(region.getKey()), region.getValue());
		}
		return mapWritable;
	}

	@Override
	public IntWritable[] findEntity(long id) {
		try{
			Set<IntWritable> addresses = new HashSet<IntWritable>();
			String[] hosts;
			Set<String> hostSet = new HashSet<String>();
			if(HGraphIds.isVertexId(id)){
				long vid = HGraphIds.extractEntityId(id);
				hosts = store.bestPlacesForVertex(vid);
			}else if(HGraphIds.isEdgeId(id)){
				long eid = HGraphIds.extractEntityId(id);
				hosts = store.bestPlacesForVertex(eid);
			}else{
				throw new HdglException("Invalid id");
			}
			for(String str:hosts){
				hostSet.add(str);
			}
			for(Map.Entry<Integer, InetSocketAddressWritable> map:regions.entrySet()){
				if(hostSet.contains(map.getValue().getHost())){
					addresses.add(new IntWritable(map.getKey()));
				}
			}
			return addresses.toArray(new IntWritable[0]);
		}catch (IOException e) {
			throw new HdglException(host);
		}
	}

	StateMachine parse(String query) throws BadQueryException{
		try{
			QueryLexer lexer=new QueryLexer(new ANTLRStringStream(query));
			QueryParser parser = new QueryParser(new TokenRewriteStream(lexer));
			Expression q = QueryCompletion.complete(parser.expression());
			SimpleStateMachine stm = QueryToStateMachine.convert(q);
			StateMachine fstm = stm.buildStateMachine();
			return fstm;
		}catch (RecognitionException e) {
			throw new BadQueryException(query, e);
		}
	}
	
	@Override
	public int prepareQuery(String query) throws BadQueryException {
		try{
			StateMachine stm = parse(query);	
			String idPath = zk().create(StringHelper.makePath(HConf.getZKQuerySessionRoot(conf), "q"), WritableHelper.toBytes(stm), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			int id = StringHelper.getLastInt(idPath);
			return id;
		}catch (Exception e) {
			throw new HdglException(e);
		}
	}

	@Override
	public IntWritable[] query(int queryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(WatchedEvent event) {
		if(event.getPath()!=null && event.getPath().startsWith(HConf.getZKRegionRoot(conf))){
			updateRegions();			
		}
	}

	@Override
	public void regionStart() {
		updateRegions();
	}

	@Override
	public void regionStop() {
		updateRegions();
	}
	
}
