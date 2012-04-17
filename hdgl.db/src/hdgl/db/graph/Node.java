package hdgl.db.graph;

/**
 * 所有节点类的基类
 * @author elm
 *
 */
public abstract class Node {
	
	protected long id;
	
	public Node(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}
	
}
