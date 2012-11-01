package hdgl.db.impl.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class HConf {

	public static final String ZK_SERVER="hdgl.zookeeper.servers";
	public static final String ZK_SESSION_TIMEOUT="hdgl.zookeeper.timeout";
	public static final String HDGL_GRAPH_NS="hdgl.graph.namespace";
	public static final String HDGL_STORE_RECORD_SIZE="hdgl.store.recordsize";
	public static final String HDGL_REGION_PORT="hdgl.regionserver.port";
	public static final String HDGL_REGION_HOST="hdgl.regionserver.host";
	public static class Defaults{
		public static final String ZK_SERVER="localhost:2181";
		public static final int ZK_SESSION_TIMEOUT = 60000;
		public static final String HDGL_GRAPH_NS="graph";
		public static final String HDGL_STORE_RECORD_SIZE = "4k";
		public static final int HDGL_REGION_PORT = 5367;
		public static final String HDGL_REGION_HOST = "localhost";
		
	}
	
	public static Configuration getConfiguration(){
		return new Configuration();
	}
	
	public static ZooKeeper getZooKeeper(Configuration conf,Watcher watcher) throws IOException{
		return new ZooKeeper(conf.get(ZK_SERVER, Defaults.ZK_SERVER),conf.getInt(ZK_SESSION_TIMEOUT, Defaults.ZK_SESSION_TIMEOUT),watcher);
	}
	
	public static FileSystem getFileSystem(Configuration conf) throws IOException{
		return FileSystem.get(conf);
	}
	
	public  static String getGraphNamespace(Configuration conf) {
		return conf.get(HDGL_GRAPH_NS,Defaults.HDGL_GRAPH_NS);
	}
	
	public static int getARecordSize(Configuration conf){
		return getNumberWithUnit(conf.get(HDGL_STORE_RECORD_SIZE, Defaults.HDGL_STORE_RECORD_SIZE));
	}
	
	public static int getNumberWithUnit(String num){
		if(num.endsWith("k")||num.endsWith("K")){
			return Integer.parseInt(num.substring(0, num.length()-1))*1024;
		}else if(num.endsWith("m")||num.endsWith("M")){
			return Integer.parseInt(num.substring(0, num.length()-1))*1024*1024;
		}else{
			return Integer.parseInt(num);
		}
	}
	
	public static int getRegionServerPort(Configuration conf){
		return conf.getInt(HDGL_REGION_PORT, Defaults.HDGL_REGION_PORT);
	}
	
	public static String getRegionServerHost(Configuration conf){
		return conf.get(HDGL_REGION_HOST, Defaults.HDGL_REGION_HOST);
	}
	
}

