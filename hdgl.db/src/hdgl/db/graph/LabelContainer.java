package hdgl.db.graph;

import hdgl.db.task.AsyncResult;

/**
 * 拥有标签的实体（顶点和边）的公用接口
 * @author elm
 *
 */
public abstract class LabelContainer {

	/**
	 * 获取标签最新的时间戳
	 * @param name 标签名
	 * @return 标签最新的时间戳
	 */
	public abstract long getTimestamp(String name);
	
	/**
	 * 获取标签最新的值，如果标签不存在，返回null。
	 * @param name 标签名
	 * @return 标签的值
	 */
	public abstract Object getLabel(String name);
	
	/**
	 * 强类型的获取标签值最新的方法。如果标签不存在或者类型不匹配，都会返回null
	 * @param name 标签名
	 * @param type 要获取的数据类型
	 * @return type类型的标签的值
	 */
	public <T> T getLabel(String name, Class<T> type){
		try{
			return type.cast(getLabel(name));
		}catch (ClassCastException e) {
			return null;
		}
	}
	
	/**
	 * 获取标签的历史更新时间戳，顺序为降序（从最新到最旧），包括当前最新的时间戳。
	 * <b>获取历史时间戳可能会很慢，所以使用异步接口调用</b>
	 * <p>此方法有可能不被支持，此时会抛出UnsupportedOperationException。
	 * 要测试此方法是否被支持，请使用Graph.supportHistory()</p>
	 * @param name 标签名
	 * @return 历史时间戳列表的异步结果
	 * @throws UnsupportedOperationException 如果该图不支持检索历史值
	 */
	public abstract AsyncResult<Iterable<Long>> getHistoricalTimestamps(String name) throws UnsupportedOperationException;
	
	/**
	 * 获取截止到timestamp时刻的最新的值，即值的时间戳小于等于timestamp的值的集合中时间戳最大的值。
	 * 如果该值在timestamp时刻之前并不存在，则返回null
	 * <b>获取历史的值可能会很慢，所以使用异步接口调用</b>
	 * <p>此方法有可能不被支持，此时会抛出UnsupportedOperationException。
	 * 要测试此方法是否被支持，请使用Graph.supportHistory()</p>
	 * 
	 * @param name 要获取的值
	 * @param timestamp 截止的时间戳
	 * @return 返回截止到timestamp时刻的最新的值的异步接口 
	 * @throws UnsupportedOperationException 如果该图不支持检索历史值
	 */
	public abstract AsyncResult<Object> getHistoricalLabel(String name,long timestamp) throws UnsupportedOperationException;
	
	/**
	 * getHistoricalLabel(String name,long timestamp)的强类型版本
	 * 获取截止到timestamp时刻的最新的值，即值的时间戳小于等于timestamp的值的集合中时间戳最大的值。
	 * 如果该值在timestamp时刻之前并不存在，或者并不是type类型，则返回null
	 * <b>获取历史的值可能会很慢，所以使用异步接口调用</b>
	 * <p>此方法有可能不被支持，此时会抛出UnsupportedOperationException。
	 * 要测试此方法是否被支持，请使用Graph.supportHistory()</p>
	 * 
	 * @see getHistoricalLabel(String name,long timestamp)
	 * @param name 要获取的值
	 * @param timestamp 截止的时间戳
	 * @param type 要获取的值的类型
	 * @return 返回截止到timestamp时刻的最新的值的异步接口
	 * @throws UnsupportedOperationException 如果该图不支持检索历史值
	 *
	 */
	public abstract <T> AsyncResult<T> getHistoricalLabel(String name,long timestamp,Class<T> type) throws UnsupportedOperationException;
	
}
