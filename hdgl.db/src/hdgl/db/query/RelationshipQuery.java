package hdgl.db.query;

import hdgl.db.graph.Node;
import hdgl.db.graph.Relationship;
import hdgl.db.task.AsyncResult;

public interface RelationshipQuery<R extends Relationship>{
	
	/**
	 * 获取结果集中只属于某一个类型的边
	 * @param type
	 * @return
	 */
	public <RS extends R> RelationshipQuery<RS> ofType(Class<RS> type);
	
	/**
	 * 将结果集强制转为另一个类型。转换到的类型应该为N的超类，否则应该使用ofType
	 * @param type
	 * @return
	 * @throws ClassCastException 如果要转换到的类型不是N的超类
	 */
	public <RS extends Relationship> RelationshipQuery<RS> as(Class<RS> type) throws ClassCastException;
	
	
	/**
	 * 获取属性值符合操作符条件的子集
	 * @param labelName
	 * @param op
	 * @param labelValue
	 * @return
	 * @see hdgl.db.query.Op
	 */
	public RelationshipQuery<R> filter(String labelName, String op, Object labelValue);
	
	/**
	 * 合并两个结果集。传入的参数的结果类型应该是当前结果类型的子类。
	 * 如果两个结果类型之间无继承关系，应当将其中一个使用as方法转换为二者的公共超类
	 * 合并之后的结果集是无序、无重复的，这意味着两个结果集的结果将混在一起，而不是简单的连接起来。
	 * @param other
	 * @return
	 */
	public RelationshipQuery<R> union(RelationshipQuery<? extends R> other);
	
	/**
	 * 对两个结果集求交。
	 * 该方法返回的结果集是无序的，即使传入参数是有序的。
	 * @param other
	 * @return
	 */
	public RelationshipQuery<R> intersect(RelationshipQuery<?> other);
	
	/**
	 * 从当前结果集中除去另一个结果集的内容
	 * @param other
	 * @return
	 */
	public RelationshipQuery<R> exclude(RelationshipQuery<?> other);
	
	/**
	 * 执行查询并返回结果集
	 * @return
	 */
	public AsyncResult<Iterable<R>> results();
}
