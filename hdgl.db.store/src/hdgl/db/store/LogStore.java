package hdgl.db.store;


import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;


public interface LogStore{

	public void writeLog(Log log) throws IOException;
	
	public FileStatus close() throws IOException;
	
}
