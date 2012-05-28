package hdgl.db.graph;

/**
 * 代表图中一条简单路径
 * @author hadoop
 *
 */
public abstract class Path {

	/**
	 * 获取该路径上节点的集合
	 * @return
	 */
	public abstract Iterable<Node> nodes();
	
	/**
	 * 获取该路径中节点的数量
	 * @return
	 */
	public abstract int length();
	
	/**
	 * 获取该路径上边的集合
	 * @return
	 */
	public abstract Iterable<Relationship> relationships();
	
	/**
	 * 获取该路径是否包括头节点。
	 * 如果此值为false，代表该路径由一条边开始。
	 * @return
	 */
	public abstract boolean includeHead();
	
	/**
	 * 获取该路径是否包括尾节点。
	 * 如果此值为false，代表该路径由一条边结束。
	 * @return
	 */
	public abstract boolean includeTail();
}
