package hdgl.db.server;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import hdgl.db.conf.GraphConf;

public class HConf {

	public static ZooKeeper getZooKeeper(Configuration conf,Watcher watcher) throws IOException{
		return new ZooKeeper(conf.get(GraphConf.ZK_SERVER, GraphConf.Defaults.ZK_SERVER),conf.getInt(GraphConf.ZK_SESSION_TIMEOUT, GraphConf.Defaults.ZK_SESSION_TIMEOUT),watcher);
	}
	
	public static FileSystem getFileSystem(Configuration conf) throws IOException{
		return FileSystem.get(conf);
	}
}
