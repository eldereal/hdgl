package hdgl.db.impl.memory;

import hdgl.db.graph.deprecated.Relationship;
import hdgl.db.graph.deprecated.RelationshipType;
import hdgl.db.graph.deprecated.Vertex;
import hdgl.db.graph.query.deprecated.RelationshipQuery;

public class MemNode implements Vertex {

	MemGraph graph;
	
	public MemNode(MemGraph graph){
		this.graph = graph;
	}
	
	@Override
	public RelationshipQuery<Relationship> getRelationships(
			RelationshipType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RelationshipQuery<Relationship> getRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

}
