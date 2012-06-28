package hdgl.db.graph;

/**
 * 图上的抽象边，该类只用于表示拓扑结构，与label无关
 * @author hadoop
 *
 */
public interface Edge {

	public Node getStartNode();
	
	public Node getEndNode();
	
	public Node getOtherNode(Node one) throws HdglException;
	
}
