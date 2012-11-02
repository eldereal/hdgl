package hdgl.db.store;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;


import hdgl.db.conf.GraphConf;
import hdgl.util.StringHelper;

public class HConf {

	public static FileSystem getFileSystem(Configuration conf) throws IOException{
		final FileSystem fs =  FileSystem.get(conf);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					fs.close();
				} catch (IOException e) {
					
				}
			}
		}));
		return fs;
	}
}
