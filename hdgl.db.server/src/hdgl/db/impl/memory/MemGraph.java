package hdgl.db.impl.memory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.collections.map.HashedMap;

import hdgl.db.graph.deprecated.Graph;
import hdgl.db.graph.deprecated.MutableGraph;
import hdgl.db.graph.deprecated.Node;
import hdgl.db.graph.deprecated.Relationship;
import hdgl.db.graph.query.deprecated.NodeQuery;
import hdgl.db.graph.query.deprecated.PathQuery;
import hdgl.db.graph.query.deprecated.RelationshipQuery;
import hdgl.db.task.AsyncCallback;
import hdgl.db.task.AsyncResult;

public class MemGraph extends MemLabelContainer implements Graph {

	public static final Tuple NULL = new Tuple(0);
	
	Graph baseGraph;
	
	HashMap<Node,Vector<Node>> rels= new HashMap<Node, Vector<Node>>();
	
	
	public MemGraph(){
		this.baseGraph = null;
	}
	
	public MemGraph(Graph baseGraph){
		super(baseGraph);
		this.baseGraph = baseGraph;
	}	
	

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long timestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long datetimeToTimestamp(Date datetime) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MutableGraph beginModify() throws IOException, InterruptedException {
		return new MemMutableGraph(this);
	}

	@Override
	public NodeQuery<Node> nodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RelationshipQuery<Relationship> relationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathQuery paths() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGlobalCallback(AsyncCallback<Object> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGlobalCallback(AsyncCallback<Object> callback) {
		// TODO Auto-generated method stub

	}

}
