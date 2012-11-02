package hdgl.db.store;

import java.io.IOException;

import hdgl.db.store.impl.hdfs.HdfsGraphStore;
import hdgl.db.store.impl.hdfs.HdfsLogStore;

import org.apache.hadoop.conf.Configuration;

public class StoreFactory {
	
	public static GraphStore createGraphStore(Configuration conf) throws IOException{
		return new HdfsGraphStore(conf);
	}
	
	public static LogStore createLogStore(Configuration conf, int sessionId) throws IOException{
		return new HdfsLogStore(conf, sessionId);
	}
}
