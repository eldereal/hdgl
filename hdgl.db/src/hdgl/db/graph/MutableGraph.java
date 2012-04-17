package hdgl.db.graph;

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
}
