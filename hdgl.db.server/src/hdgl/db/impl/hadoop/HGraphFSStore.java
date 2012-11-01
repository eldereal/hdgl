package hdgl.db.impl.hadoop;

import java.io.IOException;
import java.util.EnumSet;

import hdgl.db.graph.deprecated.HdglException;
import hdgl.db.impl.hadoop.logio.Log;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Options.CreateOpts;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Metadata;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

public class HGraphFSStore {

	String GRAPH_ROOT = "/hdgl/%s/";
	String SESSIN_ROOT = "/hdgl/%s/s/";
	
	Configuration configuration;
	FileSystem fs;
	String ns;
	int sessionId;
	Writer logs;
	Path sessionPath;
	
	public HGraphFSStore(Configuration configuration, FileSystem fs, int sessionId) throws IOException{
		this.configuration = configuration;
		this.fs = fs;
		this.ns = HConf.getGraphNamespace(configuration);
		this.sessionId = sessionId;
		initConf();
	}
	
	private void initConf() throws IOException{
		GRAPH_ROOT = String.format(GRAPH_ROOT, ns);
		SESSIN_ROOT = String.format(SESSIN_ROOT, ns);
		sessionPath = new Path(SESSIN_ROOT, Integer.toString(sessionId));
		init();
	}
	
	private void init() throws IOException{
		if(!fs.exists(new Path(GRAPH_ROOT))){
			fs.mkdirs(new Path(GRAPH_ROOT));
			fs.mkdirs(new Path(SESSIN_ROOT));
		}
	}
	
	public void initSession() throws IOException{
		if(fs.exists(sessionPath)){
			fs.delete(sessionPath, true);
		}
	}
	
	public void openLog() throws InterruptedException, IOException{
		Path logPath=new Path(sessionPath, "log");
		if(!fs.exists(logPath)){
			CompressionCodecFactory factory=new CompressionCodecFactory(configuration);
			Metadata meta=new Metadata();
			logs = SequenceFile.createWriter(FileContext.getFileContext(configuration),configuration,logPath,IntWritable.class,Log.class,
					CompressionType.BLOCK,factory.getCodec(logPath),meta,EnumSet.of(CreateFlag.CREATE),CreateOpts.createParent());
				
		}else{
			throw new HdglException.ConcurrentModifyInOneSessionException();
		}
	}
	
	public void appendLog(int key, Log log) throws IOException, InterruptedException{
		if(logs==null){
			openLog();
		}
		logs.append(new IntWritable(key), log);
	}
	
	public void closeLog() throws IOException{
		logs.close();
	}
	
	public void abortLog() throws IOException{
		try{
			logs.close();
		} catch (IOException e) {
			
		} finally {
			Path logPath=new Path(SESSIN_ROOT, Integer.toString(sessionId));
			fs.delete(logPath, true);
		}
	}
	
}
