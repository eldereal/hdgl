package hdgl.db.impl.hadoop;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.ipc.RPC;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import hdgl.db.graph.deprecated.Graph;
import hdgl.db.graph.deprecated.HdglException;
import hdgl.db.graph.deprecated.MutableGraph;
import hdgl.db.graph.deprecated.Node;
import hdgl.db.graph.deprecated.Relationship;
import hdgl.db.graph.query.deprecated.NodeQuery;
import hdgl.db.graph.query.deprecated.PathQuery;
import hdgl.db.graph.query.deprecated.RelationshipQuery;
import hdgl.db.task.AsyncCallback;
import hdgl.db.task.AsyncResult;

public class HGraph extends HLabelContainer implements Graph, Watcher {

	
	private static Logger Log = Logger.getLogger(HGraph.class);
	
	Configuration configuration;
	int sessionId;
	ZooKeeper zk;
	HGraphZKStore zkstore;
	FileSystem fs;
	HGraphFSStore fsstore;
	String ns;
	
	public HGraph(Configuration conf) throws InterruptedException, IOException {
		super(0);
		configuration = conf;
		zk = HConf.getZooKeeper(configuration, this);
		zkstore = new HGraphZKStore(conf, zk);
		sessionId = zkstore.allocSessionId();
		
		fs = HConf.getFileSystem(conf);
		fsstore = new HGraphFSStore(conf, fs, sessionId);
		fsstore.initSession();
		
		ns = HConf.getGraphNamespace(conf);
		Log.debug("New Graph Session: " + sessionId);
	}
	
	@Override
	public long timestamp() {
		return new Date().getTime();
	}

	@Override
	public long datetimeToTimestamp(Date datetime) {
		return datetime.getTime();
	}

	@Override
	public MutableGraph beginModify(){
		Log.debug("New MutableGraph: " + sessionId);
		return new HMutableGraph(configuration, this, sessionId, zkstore, fsstore);	
	}

	@Override
	public NodeQuery<Node> nodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RelationshipQuery<Relationship> relationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathQuery paths() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGlobalCallback(AsyncCallback<Object> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGlobalCallback(AsyncCallback<Object> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getTimestamp(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getLabel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncResult<Iterable<Long>> getHistoricalTimestamps(String name)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncResult<Object> getHistoricalLabel(String name, long timestamp)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> AsyncResult<T> getHistoricalLabel(String name, long timestamp,
			Class<T> type) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(WatchedEvent arg0) {
		
	}

	
	@Override
	public void close() throws IOException {
		try {
			zk.close();
		} catch (InterruptedException e) {
			throw new HdglException(e);
		} finally{
			fs.close();
		}
	}

}
