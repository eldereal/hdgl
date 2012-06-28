package hdgl.db.graph;

import hdgl.db.task.AsyncResult;

public class Relationship implements LabelContainer, Edge {
	
	private int id;	
	private LabelContainer labelImpl;
	private Edge edgeImpl;
	
	public Relationship(int id, LabelContainer labelImpl, Edge edgeImpl){
		this.id = id;
		this.labelImpl = labelImpl;
		this.edgeImpl = edgeImpl;
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
	public Node getStartNode() {
		return edgeImpl.getStartNode();
	}

	@Override
	public Node getEndNode() {
		return edgeImpl.getEndNode();
	}

	@Override
	public Node getOtherNode(Node one) throws HdglException {
		return edgeImpl.getOtherNode(one);
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
		
	
}
