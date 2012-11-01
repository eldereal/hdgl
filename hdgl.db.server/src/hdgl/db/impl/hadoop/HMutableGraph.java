package hdgl.db.impl.hadoop;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.apache.hadoop.conf.Configuration;
import org.apache.zookeeper.ZooKeeper;

import hdgl.db.graph.deprecated.Edge;
import hdgl.db.graph.deprecated.HdglError;
import hdgl.db.graph.deprecated.HdglException;
import hdgl.db.graph.deprecated.LabelContainer;
import hdgl.db.graph.deprecated.MutableGraph;
import hdgl.db.graph.deprecated.Node;
import hdgl.db.graph.deprecated.Relationship;
import hdgl.db.graph.deprecated.RelationshipType;
import hdgl.db.graph.deprecated.Vertex;
import hdgl.db.graph.query.deprecated.RelationshipQuery;
import hdgl.db.impl.hadoop.logio.Log;
import hdgl.db.task.AsyncResult;
import hdgl.db.task.CallableAsyncResult;

public class HMutableGraph implements MutableGraph{

	public class NewVertexImpl implements Vertex{

		@Override
		public RelationshipQuery<Relationship> getRelationships(
				RelationshipType type) {
			throw new HdglError.CannotQueryNewEntity();
		}

		@Override
		public RelationshipQuery<Relationship> getRelationships() {
			throw new HdglError.CannotQueryNewEntity();
		}
		
	}
	
	public class NewEdgeImpl implements Edge{

		public Node start;
		public Node end;
		
		public NewEdgeImpl(Node start, Node end) {
			super();
			this.start = start;
			this.end = end;
		}

		@Override
		public Node getStartNode() {
			return start;
		}

		@Override
		public Node getEndNode() {
			return end;
		}

		@Override
		public Node getOtherNode(Node one) throws HdglException {
			if(start.equals(one)){
				return end;
			}else if(end.equals(one)){
				return start;
			}else{
				throw new HdglException.NodeNotFound();
			}
		}
		
	}
	
	public class NewLabelContainer implements LabelContainer{

		int id;
		
		public NewLabelContainer(int id){
			this.id = id;
		}
		
		@Override
		public int getId() {
			return id;
		}

		@Override
		public long getTimestamp(String name) {
			throw new HdglError.CannotQueryNewEntity();
		}

		@Override
		public Object getLabel(String name) {
			throw new HdglError.CannotQueryNewEntity();
		}

		@Override
		public <T> T getLabel(String name, Class<T> type) {
			throw new HdglError.CannotQueryNewEntity();
		}

		@Override
		public AsyncResult<Iterable<Long>> getHistoricalTimestamps(String name)
				throws UnsupportedOperationException {
			throw new HdglError.CannotQueryNewEntity();
		}

		@Override
		public AsyncResult<Object> getHistoricalLabel(String name,
				long timestamp) throws UnsupportedOperationException {
			throw new HdglError.CannotQueryNewEntity();
		}

		@Override
		public <T> AsyncResult<T> getHistoricalLabel(String name,
				long timestamp, Class<T> type)
				throws UnsupportedOperationException {
			throw new HdglError.CannotQueryNewEntity();
		}
		
	}
	
	
	
	Configuration configuration;
	int sessionId;
	HGraph graph;
	HGraphZKStore zk;
	HGraphFSStore fs;
	String ns;
	
	public HMutableGraph(Configuration configuration, HGraph graph,
			int sessionId, HGraphZKStore zk, HGraphFSStore fs) {
		super();
		this.configuration = configuration;
		this.graph = graph;
		this.sessionId = sessionId;
		this.zk = zk;
		this.fs = fs;
		this.ns = HConf.getGraphNamespace(configuration);
	}
	
	private void appendLog(int key, Log log) throws IOException, InterruptedException{
		fs.appendLog(key, log);
	}

	@Override
	public AsyncResult<Boolean> commit(int conflictPolicy) {
		return new CallableAsyncResult<Boolean>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				fs.closeLog();
				
				return true;
			}
		});
	}

	@Override
	public AsyncResult<Boolean> commit() {
		return commit(IGNORE_ON_CONFLICT);
	}

	@Override
	public void rollback() throws IOException {
		fs.abortLog();
	}

	@Override
	public void setLabel(LabelContainer container, String name, Object value) throws IOException, InterruptedException {
		appendLog(container.getId(),Log.modifyLabel(container.getId(), zk.getOrAllocLabelId(name), value, configuration));
	}

	@Override
	public void removeLabel(LabelContainer container, String name) throws IOException, InterruptedException {
		appendLog(container.getId(),Log.deleteLabel(container.getId(), zk.getOrAllocLabelId(name)));
	}

	@Override
	public <N extends Node> N createNode(Class<N> nodeType) throws IOException, InterruptedException {
		int typeId = zk.getOrAllocTypeId(nodeType);
		int nodeId = zk.allocNodeId();
		NewLabelContainer labels = new NewLabelContainer(nodeId);
		setLabel(labels, "typeId", typeId);
		NewVertexImpl vertex = new NewVertexImpl();
		try {
			appendLog(nodeId,Log.createNode(nodeId, typeId));
			return nodeType.getConstructor(int.class, LabelContainer.class, Vertex.class)
					.newInstance(nodeId, labels, vertex);
		} catch (IllegalArgumentException e) {
			throw new HdglException(e);
		} catch (SecurityException e) {
			throw new HdglException(e);
		} catch (InstantiationException e) {
			throw new HdglException(e);
		} catch (IllegalAccessException e) {
			throw new HdglException(e);
		} catch (InvocationTargetException e) {
			throw new HdglException(e);
		} catch (NoSuchMethodException e) {
			throw new HdglException(e);
		}
	}

	@Override
	public <R extends Relationship> R createRelationship(Node start, Node end,
			Class<R> relationshipType) throws IOException, InterruptedException {
		int typeId = zk.getOrAllocTypeId(relationshipType);
		int relId = zk.allocRelationshipId();
		NewLabelContainer labels = new NewLabelContainer(relId);
		setLabel(labels, "typeId", typeId);
		NewEdgeImpl edge = new NewEdgeImpl(start, end);
		try {
			appendLog(relId, Log.createRelationShip(relId, typeId, start.getId(), end.getId()));
			return relationshipType.getConstructor(int.class, LabelContainer.class, Edge.class)
					.newInstance(relId,labels,edge);
		} catch (IllegalArgumentException e) {
			throw new HdglException(e);
		} catch (SecurityException e) {
			throw new HdglException(e);
		} catch (InstantiationException e) {
			throw new HdglException(e);
		} catch (IllegalAccessException e) {
			throw new HdglException(e);
		} catch (InvocationTargetException e) {
			throw new HdglException(e);
		} catch (NoSuchMethodException e) {
			throw new HdglException(e);
		}
	}

	@Override
	public void removeNode(Node node) throws IOException, InterruptedException {
		appendLog(node.getId(),Log.deleteNode(node.getId()));
	}

	@Override
	public void removeRelationship(Relationship relationship) throws IOException, InterruptedException {
		appendLog(relationship.getId(),Log.deleteRelationship(relationship.getId()));		
	}

}
