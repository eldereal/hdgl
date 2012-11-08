package hdgl.db.protocol;

import org.apache.hadoop.io.Writable;

public class ResultPackWritable implements Writable{

	long[][] result;
	
	boolean hasMore;

	public ResultPackWritable(long[][] result, boolean hasMore) {
		super();
		this.result = result;
		this.hasMore = hasMore;
	}

	public ResultPackWritable() {
		super();
	}
	
	
	
}
