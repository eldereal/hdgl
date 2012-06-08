package hdgl.db.graph;

import hdgl.db.graph.query.NodeQuery;
import hdgl.db.graph.query.PathQuery;
import hdgl.db.graph.query.RelationshipQuery;
import hdgl.db.task.AsyncCallback;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;

/**
 * 对一个图的只读访问接口
 * 
 * @author elm
 *
 */
public interface Graph extends LabelContainer, Closeable {

	
	/**
	 * 获取本图使用的当前的时间戳
	 * @return
	 */
	public abstract long timestamp();
	
	/**
	 * 将某个日期转换为本图使用的时间戳
	 * @param datetime
	 * @return
	 */
	public abstract long datetimeToTimestamp(Date datetime);
	
	/**
	 * 开始对图进行修改。返回一个新的修改句柄
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public abstract MutableGraph beginModify() throws IOException, InterruptedException;
	
	/**
	 * 返回图中所有顶点的结果集
	 * @return
	 */
	public abstract NodeQuery<Node> nodes();
	
	/**
	 * 返回图中所有边的结果集
	 * @return
	 */
	public abstract RelationshipQuery<Relationship> relationships();
	
	/**
	 * 返回图中所有简单路径的集合
	 * @return
	 */
	public abstract PathQuery paths();
	
	/**
	 * 添加全局的任务监听器，图中任何发生的异步任务都会触发添加的监听器。
	 * @param callback
	 */
	public abstract void addGlobalCallback(AsyncCallback<Object> callback);
	
	/**
	 * 删除全局的任务监听器
	 * @param callback
	 */
	public abstract void removeGlobalCallback(AsyncCallback<Object> callback);
}
