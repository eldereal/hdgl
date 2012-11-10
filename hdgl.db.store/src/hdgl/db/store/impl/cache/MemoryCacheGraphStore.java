package hdgl.db.store.impl.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.WeakHashMap;

import com.sun.org.apache.bcel.internal.generic.NEW;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.Entity;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;
import hdgl.db.store.IndexGraphStore;

public class MemoryCacheGraphStore implements IndexGraphStore {

	static final int CACHE_SIZE = 512;
	
	WeakHashMap<Long, Entity> cache = new WeakHashMap<Long, Entity>();
	
	GraphStore cached;
	
	public MemoryCacheGraphStore(GraphStore cached){
		this.cached = cached;
	}
	
	@Override
	public InputStream getVertexData(long id) throws IOException {
		return cached.getVertexData(id);
	}

	@Override
	public InputStream getEdgeData(long id) throws IOException {
		return cached.getEdgeData(id);
	}

	@Override
	public Vertex parseVertex(long id) throws IOException {
		if(cache.containsKey(id)){
			return (Vertex) cache.get(id);
		}else{
			Vertex v = cached.parseVertex(id);
			cache.put(id, v);
			return v;
		}
	}

	@Override
	public Edge parseEdge(long id) throws IOException {
		if(cache.containsKey(id)){
			return (Edge) cache.get(id);
		}else{
			Edge e = cached.parseEdge(id);
			cache.put(id, e);
			return e;
		}
	}

	@Override
	public String[] bestPlacesForVertex(long entityId) throws IOException {
		return cached.bestPlacesForVertex(entityId);
	}

	@Override
	public String[] bestPlacesForEdge(long entityId) throws IOException {
		return cached.bestPlacesForEdge(entityId);
	}

	@Override
	public long getVertexCount() throws IOException {
		return cached.getVertexCount();
	}

	@Override
	public long getVertexCountPerBlock() throws IOException {
		return cached.getVertexCountPerBlock();
	}

	@Override
	public long getEdgeCount() throws IOException {
		return cached.getEdgeCount();
	}

	@Override
	public long getEdgeCountPerBlock() throws IOException {
		return cached.getEdgeCountPerBlock();
	}

	@Override
	public void close() {
		cached.close();
	}

	@Override
	public Iterable<Long> findVertexByLabelInRange(String label, byte[] min,
			byte[] max) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public Iterable<Long> findEdgeByLabelInRange(String label, byte[] min,
			byte[] max) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public long findVertexByLabelValue(String label, byte[] val) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public long findEdgeByLabelValue(String label, byte[] val) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public void prepareIndex() {
		
	}

}
