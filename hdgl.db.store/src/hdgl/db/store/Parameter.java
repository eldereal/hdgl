package hdgl.db.store;

public class Parameter {
	static final int REDUCER_NUMBER = 1;
	static final int REGULAR_BLOCK_SIZE = 40;
	static final int OFFSET_MAX_LEN = 8;
	static final int LENGTH_MAX_LEN = 4;
	static final String CONF_ARG1 = "fs.defaultFS";
	static final String CONF_ARG2 = "hdfs://localhost:9000";
	static final String IN_PATH = "logdata";
	static final String OUT_PATH = "result";
	static final String VERTEX_IRREGULAR_FILE_NAME = "VertexIrregular";
	static final String EDGE_IRREGULAR_FILE_NAME = "EdgeIrregular";
	static final String VERTEX_REGULAR_FILE_NAME = "VertexRegular";
	static final String EDGE_REGULAR_FILE_NAME = "EdgeRegular";
	static int VertexNumber = 5;
	static int EdgeNumber = 9;
}
