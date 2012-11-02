package hdgl.db.store.impl.hdfs;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import hdgl.db.conf.GraphConf;
import hdgl.db.store.HConf;
import hdgl.db.store.Log;
import hdgl.db.store.LogStore;

public class HdfsLogStore implements LogStore {

	Configuration configuration;
	int sessionId;
	FileSystem fs;
	FileStatus logfile;
	FSDataOutputStream outputStream;
	
	public HdfsLogStore(Configuration configuration, int sessionId) throws IOException {
		super();
		this.configuration = configuration;
		this.sessionId = sessionId;
		this.fs = HConf.getFileSystem(configuration);
		Path sessionRoot = new Path(GraphConf.getGraphRoot(configuration),"s"+sessionId);
		Path logPath = new Path(sessionRoot, "log");
		outputStream = fs.append(logPath);
	}

	@Override
	public void writeLog(Log log) throws IOException {
		log.write(outputStream);
	}

	@Override
	public FileStatus close() throws IOException {
		try{
			outputStream.close();
			return logfile;
		}finally{
			fs.close();
		}
	}

}
