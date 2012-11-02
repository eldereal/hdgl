package hdgl.db.store.impl.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import hdgl.db.conf.GraphConf;
import hdgl.db.store.GraphStore;
import hdgl.db.store.HConf;

public class HdfsGraphStore implements GraphStore {
	
	//vertex fixed length file
	FileStatus v_f;
	//vertex vary length file
	FileStatus v_v;
	//edge fixed length file
	FileStatus e_f;
	//edge vary length file
	FileStatus e_v;
	
	FileSystem fs;
	
	int trunkSize;
	
	public HdfsGraphStore(Configuration conf) throws IOException{
		String root = GraphConf.getGraphRoot(conf);
		fs = HConf.getFileSystem(conf);
		trunkSize = GraphConf.getGraphTrunkSize(conf);
		Path rootPath = new Path(root);
		v_f = fs.getFileStatus(new Path(rootPath,"v.f"));
		v_v = fs.getFileStatus(new Path(rootPath,"v.v"));
		e_f = fs.getFileStatus(new Path(rootPath,"e.f"));
		e_v = fs.getFileStatus(new Path(rootPath,"e.v"));
	}
	
	@Override
	public InputStream getVertexData(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public InputStream getEdgeData(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InetSocketAddress[] bestPlacesForVertex(long vId) {
		return null;
	}

	@Override
	public InetSocketAddress[] bestPlacesForEdge(long entityId) {
		return null;
	}

}
