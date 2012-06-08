package hdgl.db.graph.query;

import hdgl.db.graph.Path;
import hdgl.db.task.AsyncResult;

/**
 * 图上的一个路径查询
 * @author hadoop
 *
 */
public interface PathQuery {

	
	/**
	 * 在此结果集中进行一次筛选
	 * @param regex
	 * @return
	 */
	public PathQuery filter(String regex);
	
	/**
	 * 合并另一个结果集，合并的结果是无序，无重复的。
	 * @param other
	 * @return
	 */
	public PathQuery union(PathQuery other);
	
	/**
	 * 与另一个结果集求交集
	 * @param other
	 * @return
	 */
	public PathQuery intersect(PathQuery other);
	
	/**
	 * 从结果集中除去属于另一个结果集的内容
	 * @param other
	 * @return
	 */
	public PathQuery exclude(PathQuery other);
	
	/**
	 * 进行此次查询，返回延时的结果集。
	 * @return
	 */
	public AsyncResult<Iterable<Path>> results();
	
}
