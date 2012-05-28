package hdgl.db.graph;

import java.util.Date;

/**
 * 对一个图的只读访问接口
 * 
 * @author elm
 *
 */
public abstract class Graph extends LabelContainer {

	public abstract long timestamp();
	
	public abstract long datetimeToTimestamp(Date datetime);
	
	public abstract MutableGraph beginModify();
	
	
	
}
