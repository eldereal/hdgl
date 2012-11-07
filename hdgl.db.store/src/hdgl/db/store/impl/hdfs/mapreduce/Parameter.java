package hdgl.db.store.impl.hdfs.mapreduce;

import hdgl.db.conf.GraphConf;



public class Parameter {
	
	public static final int REDUCER_NUMBER = 1;
	//public static final int REGULAR_BLOCK_SIZE = 40;
	public static final int OFFSET_MAX_LEN = 8;
	public static final int LENGTH_MAX_LEN = 4;
	//public static final String CONF_ARG1 = "fs.defaultFS";
	//public static final String CONF_ARG2 = "hdfs://localhost:9000";
	public static final String IN_PATH = "logdata";
	public static final String OUT_PATH = "result";
	public static final String VERTEX_IRREGULAR_FILE_NAME = "v_v";
	public static final String EDGE_IRREGULAR_FILE_NAME = "e_v";
	public static final String VERTEX_REGULAR_FILE_NAME = "v_f";
	public static final String EDGE_REGULAR_FILE_NAME = "e_f";
	//public static int VertexNumber = 5;
	//public static int EdgeNumber = 9;
}
