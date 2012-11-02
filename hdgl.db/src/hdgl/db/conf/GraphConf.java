package hdgl.db.conf;

import org.apache.hadoop.conf.Configuration;

public final class GraphConf {
	
	public static final String GRAPH_ROOT = "hdgl.graph.root";
	public static final String GRAPH_TRUNK_SIZE = "hdgl.graph.trunk.size";
	public static final String DEFAULT_FS = "default.fs";
	public static final String ZK_SERVER = "hdgl.zookeeper.servers";
	public static final String ZK_ROOT = "hdgl.zookeeper.root";
	public static final String ZK_SESSION_TIMEOUT = "hdgl.zookeeper.timeout";
	
	public class Defaults{
		public static final String ZK_SERVER="localhost:2181";
		public static final int ZK_SESSION_TIMEOUT = 60000;
		public static final String GRAPH_ROOT = "/hdgl/graph/";
		public static final String DEFAULT_FS = "hdfs://localhost:9000/";
		public static final String ZK_ROOT = "/hdgl";
		public static final int GRAPH_TRUNK_SIZE = 4 * 1024;
		
	}
	
	public static String getZookeeperRoot(Configuration conf){
		return conf.get(ZK_ROOT, Defaults.ZK_ROOT);
	}
	
	public static String getGraphRoot(Configuration conf){
		return conf.get(GRAPH_ROOT, Defaults.GRAPH_ROOT);
	}
	
	public static int getGraphTrunkSize(Configuration conf){
		return conf.getInt(GRAPH_TRUNK_SIZE, Defaults.GRAPH_TRUNK_SIZE);
	}
}
