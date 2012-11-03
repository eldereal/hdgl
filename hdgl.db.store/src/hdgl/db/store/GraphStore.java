package hdgl.db.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public interface GraphStore {

	public InputStream getVertexData(long id) throws IOException;
	
	public InputStream getEdgeData(long id) throws IOException;
	
	public String[] bestPlacesForVertex(long entityId) throws IOException;
	
	public String[] bestPlacesForEdge(long entityId) throws IOException;
}
