package hdgl.db.graph;

import hdgl.db.graph.query.RelationshipQuery;

/**
 * 表示图上的一个顶点，和Node的不同之处在于Vertex是一个抽象顶点，只表示图的拓扑结构，不涉及label。
 * @author hadoop
 *
 */
public interface Vertex {

	/**
	 * 获取指定类型（入边、出边）的相关边的集合
	 * @param type
	 * @return
	 */
	public RelationshipQuery<Relationship> getRelationships(RelationshipType type);
	
	/**
	 * 获取与此节点关联的所有边的集合
	 * @return
	 */
	public RelationshipQuery<Relationship> getRelationships();
	
}
