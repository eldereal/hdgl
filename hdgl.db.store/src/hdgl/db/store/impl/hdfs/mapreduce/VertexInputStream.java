package hdgl.db.store.impl.hdfs.mapreduce;

import hdgl.util.StringHelper;

import java.io.IOException;

public class VertexInputStream extends GraphInputStream {
	public VertexInputStream(long id) throws IOException
	{
		super(id);

		int ret = locate(Parameter.OUT_PATH + "/" + Parameter.VERTEX_REGULAR_FILE_NAME);
		fileIrr = Parameter.OUT_PATH + "/" + Parameter.VERTEX_IRREGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(ret);
	}

}
