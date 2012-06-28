package hdgl.db.graph;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import hdgl.db.task.AsyncResult;

/**
 * 对一个图的修改访问接口
 * @author elm
 *
 */
public interface MutableGraph {
	
	/**
	 * 在提交修改时如果冲突，则忽略此条修改并继续进行下一条
	 */
	public static final int IGNORE_ON_CONFLICT = 0;
	
	/**
	 * 在提交修改时如果发生冲突，则回滚所有的修改并使整体修改失败
	 */
	public static final int FAIL_ON_CONFLICT = 1;
	
	/**
	 * 提交修改，返回的异步结果在执行过程中，系统处于非一致状态。
	 * 在异步结果完成后，系统恢复一致状态。异步结果使用布尔值代表此次提交是否成功。
	 * 如果使用了IGNORE_ON_CONFLICT作为异常策略，则返回值必定为true，
	 * 即使所有的修改操作都失败而被忽略了。
	 * 
	 * @param conflictPolicy 在提交修改发生冲突时的处理策略
	 * @return 提交操作的异步结果
	 */
	public AsyncResult<Boolean> commit(int conflictPolicy);
	
	/**
	 * 使用FAIL_ON_CONFLICT作为策略提交修改。
	 * @see MutableGraph.commit(int conflictPolicy)
	 * @return 提交操作的异步结果
	 */
	public AsyncResult<Boolean> commit();
	
	/**
	 * 取消本次修改
	 * @throws IOException 
	 */
	public void rollback() throws IOException;
	
	/**
	 * 设置标签的值
	 * @param container 标签容器
	 * @param name 标签名
	 * @param value 标签的值
	 * @throws IOException 
	 */
	public void setLabel(LabelContainer container, String name, Object value) throws InterruptedException, IOException;
	
	/**
	 * 删除一个标签
	 * @param container
	 * @param name
	 */
	public void removeLabel(LabelContainer container, String name) throws IOException, InterruptedException;
	
	/**
	 * 创建一个特定类型的顶点
	 * @param nodeType
	 * @return 
	 * @throws InterruptedException 
	 * @throws HdglException 
	 */
	public <N extends Node> N createNode(Class<N> nodeType) throws IOException, InterruptedException;
	
	/**
	 * 创建一个边
	 * @param start
	 * @param end
	 * @param relationshipType
	 * @return
	 * @throws InterruptedException 
	 * @throws HdglException 
	 */
	public <R extends Relationship> R createRelationship(Node start, Node end, Class<R> relationshipType) throws IOException, InterruptedException;
	
	/**
	 * 移除一个顶点
	 * @param node
	 * @throws HdglException 
	 * @throws InterruptedException 
	 */
	public void removeNode(Node node) throws IOException, InterruptedException;
	
	/**
	 * 移除一条边
	 * @param relationship
	 * @throws InterruptedException 
	 * @throws HdglException 
	 */
	public void removeRelationship(Relationship relationship) throws IOException, InterruptedException;
}
