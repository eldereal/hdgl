package hdgl.db.conf;

public class GraphConf {
	
	public static final String GRAPH_ROOT = "hdgl.graph.root";
	public static final String DEFAULT_FS = "default.fs";
	public static final String ZK_SERVER = "hdgl.zookeeper.servers";
	public static final String ZK_SESSION_TIMEOUT = "hdgl.zookeeper.timeout";
	
	public class Defaults{
		public static final String ZK_SERVER="localhost:2181";
		public static final int ZK_SESSION_TIMEOUT = 60000;
		public static final String GRAPH_ROOT = "/hdgl/graph/";
		public static final String DEFAULT_FS = "hdfs://localhost:9000/";
	}
	
	
}
