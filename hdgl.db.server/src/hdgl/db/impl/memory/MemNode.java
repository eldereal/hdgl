package hdgl.db.impl.memory;

import hdgl.db.graph.Relationship;
import hdgl.db.graph.RelationshipType;
import hdgl.db.graph.Vertex;
import hdgl.db.graph.query.RelationshipQuery;

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
