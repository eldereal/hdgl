package hdgl.db.impl.hadoop;

import org.apache.hadoop.conf.Configuration;

import hdgl.db.graph.LabelContainer;
import hdgl.db.graph.MutableGraph;
import hdgl.db.graph.Node;
import hdgl.db.graph.Relationship;
import hdgl.db.task.AsyncResult;

public class HMutableGraph implements MutableGraph{

	Configuration configuration;
	String sessionId;
	HGraph graph;
	
	public HMutableGraph(Configuration configuration, HGraph graph,
			String sessionId) {
		super();
		this.configuration = configuration;
		this.graph = graph;
		this.sessionId = sessionId;
	}

	@Override
	public AsyncResult<Boolean> commit(int conflictPolicy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncResult<Boolean> commit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLabel(LabelContainer container, String name, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeLabel(LabelContainer container, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <N extends Node> N createNode(Class<N> nodeType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends Relationship> R createRelationship(Node start, Node end,
			Class<R> relationshipType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeNode(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeRelationship(Relationship relationship) {
		// TODO Auto-generated method stub
		
	}

}
