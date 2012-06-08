package hdgl.db.impl.hadoop;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import hdgl.db.graph.Graph;
import hdgl.db.graph.HdglException;
import hdgl.db.graph.MutableGraph;
import hdgl.db.graph.Node;
import hdgl.db.graph.Relationship;
import hdgl.db.graph.query.NodeQuery;
import hdgl.db.graph.query.PathQuery;
import hdgl.db.graph.query.RelationshipQuery;
import hdgl.db.task.AsyncCallback;
import hdgl.db.task.AsyncResult;

public class HGraph extends HLabelContainer implements Graph, Watcher {

	
	private static final String CLIENT_ID = "/hdgl/client/";
	private static final String SESSION_ID = "/hdgl/session/";
	private static Logger Log = Logger.getLogger(HGraph.class);
	
	Configuration configuration;
	String clientId;
	ZooKeeper zk;
	
	public HGraph(Configuration conf) throws IOException, InterruptedException {
		super(0);
		try{
			configuration = conf;
			zk = HConf.getZooKeeper(configuration, this);
			clientId = zk.create(CLIENT_ID, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			Log.debug("New Graph Client: " + clientId);
		}catch (KeeperException e) {
			Log.error("Zookeeper error:", e);
			throw new HdglException(e);
		}
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
	public MutableGraph beginModify() throws IOException, InterruptedException {
		try{
			String sessionId=SESSION_ID;
			sessionId = zk.create(sessionId, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			Log.debug("New MutableGraph: "+sessionId);
			return new HMutableGraph(configuration, this, sessionId);
		}catch (KeeperException e) {
			Log.error("Zookeeper error:", e);
			throw new HdglException(e);
		}
		
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
		}
	}

}
