package hdgl.db.graph;

import hdgl.db.task.AsyncResult;

public interface MutableGraph {

	public AsyncResult<Boolean> commit();
	public AsyncResult<Boolean> abort();
	
	public Vertex createVertex(String type);
	public Edge createEdge(String type, Vertex start, Vertex end);
	
	public void setLabel(Entity entity, String name, byte[] value);
	
	public void deleteEntity(Entity e);
	public void deleteLabel(Entity e, String name);
	
}
