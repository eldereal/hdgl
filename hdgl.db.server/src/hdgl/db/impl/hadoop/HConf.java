package hdgl.db.impl.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class HConf {

	public static final String ZK_SERVER="hdgl.zookeeper.servers";
	public static final String ZK_SESSION_TIMEOUT="hdgl.zookeeper.timeout";
	public static final String HDGL_GRAPH_NS="hdgl.graph.namespace";
	
	public static class Defaults{
		public static final String ZK_SERVER="localhost:2181";
		public static final int ZK_SESSION_TIMEOUT = 60000;
		public static final String HDGL_GRAPH_NS="";
	}
	
	public static ZooKeeper getZooKeeper(Configuration conf,Watcher watcher) throws IOException{
		return new ZooKeeper(conf.get(ZK_SERVER, Defaults.ZK_SERVER),conf.getInt(ZK_SESSION_TIMEOUT, Defaults.ZK_SESSION_TIMEOUT),watcher);
	}
	
	public  static String getGraphNamespace(Configuration conf) {
		return conf.get(HDGL_GRAPH_NS,Defaults.HDGL_GRAPH_NS);
	}
	
	
}

