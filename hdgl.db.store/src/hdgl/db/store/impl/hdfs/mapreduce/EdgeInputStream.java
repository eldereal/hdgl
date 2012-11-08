package hdgl.db.store.impl.hdfs.mapreduce;

import hdgl.db.conf.GraphConf;
import hdgl.util.StringHelper;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;


public class EdgeInputStream extends GraphInputStream {
	public EdgeInputStream(long id, Configuration conf) throws IOException
	{
		super(id - 1, conf, GraphConf.getEdgeTrunkSize(conf));
		
		int ret = locate(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.EDGE_REGULAR_FILE_NAME);
		fileIrr = GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.EDGE_IRREGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(ret);
	}

}
