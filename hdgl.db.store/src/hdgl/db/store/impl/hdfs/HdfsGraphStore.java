package hdgl.db.store.impl.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import hdgl.db.conf.GraphConf;
import hdgl.db.impl.HGraphIds;
import hdgl.db.store.GraphStore;
import hdgl.db.store.HConf;
import hdgl.db.store.impl.hdfs.mapreduce.Edge;
import hdgl.db.store.impl.hdfs.mapreduce.EdgeInputStream;
import hdgl.db.store.impl.hdfs.mapreduce.Vertex;
import hdgl.db.store.impl.hdfs.mapreduce.VertexInputStream;

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
	
	public InputStream getVertexData(long id) throws IOException
	{
		VertexInputStream vis = new VertexInputStream(id);
		return vis;
	}
	
	public InputStream getEdgeData(long id) throws IOException
	{
		EdgeInputStream eis = new EdgeInputStream(id);
		return eis;
	}
	
	public Vertex parseVertex(long id) throws IOException
	{
		VertexInputStream vis = (VertexInputStream) getVertexData(id);
		Vertex v = new Vertex(vis.readInt());
		int outNum, edge, vertex, inNum, num;
		outNum = vis.readInt();
		inNum = vis.readInt();
		for (int i = 0; i < outNum; i++)
		{
			edge = vis.readInt();
			vertex = vis.readInt();
			v.addEdges(-1, edge, vertex);
		}
		for (int i = 0; i < inNum; i++)
		{
			edge = vis.readInt();
			vertex = vis.readInt();
			v.addEdges(1, edge, vertex);
		}
		num = vis.readInt();
		int len;
		for (int i = 0; i < num; i++)
		{
			len = vis.readInt();
			byte[] b = new byte[len];
			String key = null, value = null;
			if (len == vis.read(b))
			{
				key = new String(b);
			}
			len = vis.readInt();
			b = new byte[len];
			if (len == vis.read(b))
			{
				value = new String(b);
			}
			if (!((key.length() == 0) && (value.length() == 0)))
			{
				v.addLabel(key, value);
			}
		}
		return v;
	}
	
	public Edge parseEdge(long id) throws IOException
	{
		EdgeInputStream eis = (EdgeInputStream) getEdgeData(id);
		Edge e = new Edge(eis.readInt());
		e.setVertex1(eis.readInt());
		e.setVertex2(eis.readInt());
		int num;
		num = eis.readInt();
		int len;
		for (int i = 0; i < num; i++)
		{
			len = eis.readInt();
			byte[] b = new byte[len];
			String key = null, value = null;
			if (len == eis.read(b))
			{
				key = new String(b);
			}
			len = eis.readInt();
			b = new byte[len];
			if (len == eis.read(b))
			{
				value = new String(b);
			}
			e.addLabel(key, value);
		}
		return e;
	}

	@Override
	public String[] bestPlacesForVertex(long vId) throws IOException {
		vId = HGraphIds.extractEntityId(vId);
		BlockLocation[] locs = fs.getFileBlockLocations(v_f, vId * trunkSize, trunkSize);
		Set<String> hosts=new HashSet<String>();
		for(BlockLocation loc:locs){
			for(String host:loc.getHosts()){
				hosts.add(host);
			}
		}
		return hosts.toArray(new String[0]);
	}

	@Override
	public String[] bestPlacesForEdge(long entityId) throws IOException {
		entityId = HGraphIds.extractEntityId(entityId);
		BlockLocation[] locs = fs.getFileBlockLocations(e_f, entityId * trunkSize, trunkSize);
		Set<String> hosts = new HashSet<String>();
		for(BlockLocation loc:locs){
			for(String host:loc.getHosts()){
				hosts.add(host);
			}
		}
		return hosts.toArray(new String[0]);
	}

}
