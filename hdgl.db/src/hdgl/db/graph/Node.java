package hdgl.db.graph;

import hdgl.db.graph.query.RelationshipQuery;
import hdgl.db.task.AsyncResult;

/**
 * 所有节点类的基类
 * <p>要添加自定义节点类型时，需要继承此类。</p>
 * <p>自定义的节点类型必须实现NodeType(int, LabelContainer, Vertex)构造函数，并且调用超类的相同构造函数</p>
 * <p>自定义节点的属性必须通过label来设置，即调用setLabel方法，或通过设置超类的其他属性来实现(假设超类属性的实现就是基于label的)
 * 在序列化此类时只会考虑label属性，未使用label的属性都会被丢弃。
 * </p>
 * 
 * @author elm
 *
 */
public class Node implements LabelContainer,Vertex{

	private int id;	
	private LabelContainer labelImpl;
	private Vertex vertexImpl;
	
	public Node(int id, LabelContainer labelImpl, Vertex vertexImpl){
		this.id = id;
		this.labelImpl = labelImpl;
		this.vertexImpl = vertexImpl;
	}
	
	public Class<? extends Node> getType() {
		String typename = getLabel("type", String.class);
		try {
			return Class.forName(typename).asSubclass(Node.class);
		} catch (ClassNotFoundException e) {
			throw new HdglError(e);
		}
	}
	
	
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public long getTimestamp(String name) {
		return labelImpl.getTimestamp(name); 
	}

	@Override
	public Object getLabel(String name) {
		return labelImpl.getLabel(name);
	}

	@Override
	public <T> T getLabel(String name, Class<T> type) {
		return labelImpl.getLabel(name, type);
	}

	@Override
	public AsyncResult<Iterable<Long>> getHistoricalTimestamps(String name)
			throws UnsupportedOperationException {
		return labelImpl.getHistoricalTimestamps(name);
	}

	@Override
	public AsyncResult<Object> getHistoricalLabel(String name, long timestamp)
			throws UnsupportedOperationException {
		return labelImpl.getHistoricalLabel(name, timestamp);
	}

	@Override
	public <T> AsyncResult<T> getHistoricalLabel(String name, long timestamp,
			Class<T> type) throws UnsupportedOperationException {
		return labelImpl.getHistoricalLabel(name, timestamp, type);
	}

	@Override
	public RelationshipQuery<Relationship> getRelationships(
			RelationshipType type) {
		return vertexImpl.getRelationships(type);
	}

	@Override
	public RelationshipQuery<Relationship> getRelationships() {
		return vertexImpl.getRelationships();
	}
	
	
	
}
