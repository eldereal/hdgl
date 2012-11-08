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
import hdgl.db.graph.HGraphIds;
import hdgl.db.store.GraphStore;
import hdgl.db.store.HConf;
import hdgl.db.store.HEdge;
import hdgl.db.store.HVertex;
import hdgl.db.store.impl.hdfs.mapreduce.Edge;
import hdgl.db.store.impl.hdfs.mapreduce.EdgeInputStream;
import hdgl.db.store.impl.hdfs.mapreduce.Parameter;
import hdgl.db.store.impl.hdfs.mapreduce.Vertex;
import hdgl.db.store.impl.hdfs.mapreduce.VertexInputStream;
import hdgl.util.StringHelper;

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
	
	Configuration conf;
	
	int vtrunkSize;
	int etrunkSize;
	
	public HdfsGraphStore(Configuration conf) throws IOException{
		this.conf = conf;
		String root = GraphConf.getGraphRoot(conf);
		fs = HConf.getFileSystem(conf);
		vtrunkSize = GraphConf.getVertexTrunkSize(conf);
		etrunkSize = GraphConf.getEdgeTrunkSize(conf);
		Path rootPath = new Path(root);
		v_f = fs.getFileStatus(new Path(rootPath,Parameter.VERTEX_REGULAR_FILE_NAME));
		v_v = fs.getFileStatus(new Path(rootPath,Parameter.VERTEX_IRREGULAR_FILE_NAME));
		e_f = fs.getFileStatus(new Path(rootPath,Parameter.EDGE_REGULAR_FILE_NAME));
		e_v = fs.getFileStatus(new Path(rootPath,Parameter.EDGE_IRREGULAR_FILE_NAME));
	}
	
	public InputStream getVertexData(long id) throws IOException
	{
		VertexInputStream vis = new VertexInputStream(id, conf);
		return vis;
	}
	
	public InputStream getEdgeData(long id) throws IOException
	{
		EdgeInputStream eis = new EdgeInputStream(id, conf);
		return eis;
	}
	
	public hdgl.db.graph.Vertex parseVertex(long id) throws IOException
	{
		VertexInputStream vis = (VertexInputStream) getVertexData(id);
		HVertex v = new HVertex(vis.readInt(), "");
		int outNum, edge, vertex, inNum, num;
		outNum = vis.readInt();
		inNum = vis.readInt();
		for (int i = 0; i < outNum; i++)
		{
			edge = vis.readInt();
			vertex = vis.readInt();
			v.addOutEdge(edge, vertex);
		}
		for (int i = 0; i < inNum; i++)
		{
			edge = vis.readInt();
			vertex = vis.readInt();
			v.addInEdge(edge, vertex);
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
			if (!(key.length() == 0))
			{
				if (key.compareTo("type") == 0)
				{
					v.setType(new String(StringHelper.stringToBytes(value)));
				}
				else {
					v.addLabel(key, StringHelper.stringToBytes(value));
				}
			}
		}
		return v;
	}
	
	public hdgl.db.graph.Edge parseEdge(long id) throws IOException
	{
		EdgeInputStream eis = (EdgeInputStream) getEdgeData(id);
		int eid, v1, v2;
		eid = eis.readInt();
		v1 = eis.readInt();
		v2 = eis.readInt();
		HEdge e = new HEdge(eid, "", new HVertex(v1, ""), new HVertex(v2, ""));
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
			if (!(key.length() == 0))
			{
				if (key.compareTo("type") == 0)
				{
					e.setType(new String(StringHelper.stringToBytes(value)));
				}
				else {
					e.addLabel(key, StringHelper.stringToBytes(value));
				}
			}
		}
		return e;
	}

	@Override
	public String[] bestPlacesForVertex(long vId) throws IOException {
		vId = HGraphIds.extractEntityId(vId);
		BlockLocation[] locs = fs.getFileBlockLocations(v_f, vId * vtrunkSize, vtrunkSize);
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
		BlockLocation[] locs = fs.getFileBlockLocations(e_f, entityId * etrunkSize, etrunkSize);
		Set<String> hosts = new HashSet<String>();
		for(BlockLocation loc:locs){
			for(String host:loc.getHosts()){
				hosts.add(host);
			}
		}
		return hosts.toArray(new String[0]);
	}

	@Override
	public long getVertexCount() throws IOException {
		return v_f.getLen()/vtrunkSize;
	}

	@Override
	public long getEdgeCount() throws IOException {
		return e_f.getLen()/etrunkSize;
	}

	@Override
	public long getVertexCountPerBlock() throws IOException {
		return v_f.getBlockSize()/vtrunkSize;
	}

	@Override
	public long getEdgeCountPerBlock() throws IOException {
		return e_f.getBlockSize()/etrunkSize;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
}
