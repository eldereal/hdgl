package hdgl.db.impl;

import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;

public class HVertex implements Vertex {

	long id;
	String type;
	
	public HVertex(long id, String type){
		this.id = id;
		this.type = type;
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Iterable<LabelValue> getLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Edge> getOutEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Edge> getInEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Edge> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

}
