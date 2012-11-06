package hdgl.db.store;

import hdgl.db.store.impl.hdfs.mapreduce.Edge;
import hdgl.db.store.impl.hdfs.mapreduce.Vertex;

import java.io.IOException;
import java.io.InputStream;

public interface GraphStore {

	public InputStream getVertexData(long id) throws IOException;
	
	public InputStream getEdgeData(long id) throws IOException;
	
	public Vertex parseVertex(long id) throws IOException;	
	
	public Edge parseEdge(long id) throws IOException;
	
	public String[] bestPlacesForVertex(long entityId) throws IOException ;
	
	public String[] bestPlacesForEdge(long entityId) throws IOException ;
	
	public long getVertexCount() throws IOException;
	
	public long getVertexCountPerBlock() throws IOException;
	
	public long getEdgeCount() throws IOException;
	
	public long getEdgeCountPerBlock() throws IOException;
}
