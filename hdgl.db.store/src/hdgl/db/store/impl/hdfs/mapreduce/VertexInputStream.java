package hdgl.db.store.impl.hdfs.mapreduce;

import hdgl.db.conf.GraphConf;
import hdgl.util.StringHelper;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

public class VertexInputStream extends GraphInputStream {
	public VertexInputStream(long id, Configuration conf) throws IOException
	{
		super(-1 - id, conf, GraphConf.getVertexTrunkSize(conf));

		int ret = locate(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.VERTEX_REGULAR_FILE_NAME);
		fileIrr = GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.VERTEX_IRREGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(ret);
	}

}
