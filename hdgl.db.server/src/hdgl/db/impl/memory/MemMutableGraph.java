package hdgl.db.impl.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import hdgl.db.graph.deprecated.HdglException;
import hdgl.db.graph.deprecated.LabelContainer;
import hdgl.db.graph.deprecated.MutableGraph;
import hdgl.db.graph.deprecated.Node;
import hdgl.db.graph.deprecated.Relationship;
import hdgl.db.task.AsyncResult;
import hdgl.db.task.CallableAsyncResult;
import hdgl.db.task.DefiniteAsyncResult;

public class MemMutableGraph implements MutableGraph {

	MemGraph graph;
	
	public MemMutableGraph(MemGraph graph){
		this.graph = graph;
	}
	
	@Override
	public AsyncResult<Boolean> commit(int conflictPolicy) {
		return new DefiniteAsyncResult<Boolean>(true);
	}

	@Override
	public AsyncResult<Boolean> commit() {
		return commit(IGNORE_ON_CONFLICT);
	}

	@Override
	public void rollback() {
		throw new RuntimeException("MemMutableGraph is auto commit, cannot rollback");
	}

	@Override
	public void setLabel(LabelContainer container, String name, Object value) {
		if(container instanceof MemLabelContainer){
			((MemLabelContainer) container).setLabel(name, value, graph.timestamp());
		}
	}

	@Override
	public void removeLabel(LabelContainer container, String name) {
		if(container instanceof MemLabelContainer){
			((MemLabelContainer) container).removeLabel(name, graph.timestamp());
		}
	}

	@Override
	public <N extends Node> N createNode(Class<N> nodeType)
			throws HdglException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends Relationship> R createRelationship(Node start, Node end,
			Class<R> relationshipType) throws HdglException,
			InterruptedException {
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
