package hdgl.db.query;

import hdgl.db.server.bsp.BSPRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RegionQueryContext {
	
	private QueryContext context;
	private BSPRunner runner;
	private Map<Integer, Vector<long[]>> result = new HashMap<Integer, Vector<long[]>>();
	private int neededResultLength;
	private boolean complete;
	private Object resultMutex = new Object();
	private Object needMutex = new Object();
	
	public RegionQueryContext(QueryContext context, BSPRunner runner) {
		super();
		this.context = context;
		this.runner = runner;
	}

	public void waitNeed(int len) throws InterruptedException{
		while(neededResultLength<len){
			synchronized (needMutex) {
				needMutex.wait();
			}
		}
	}
	
	public void waitResult(int len) throws InterruptedException{
		while(!complete && !result.containsKey(len+1)){
			synchronized (resultMutex) {
				resultMutex.wait();
			}
		}
	}
	
	public synchronized void addResult(long[] path){
		int len=path.length;
		assert len%2==1;
		len=(len+1)/2;
		Vector<long[]> container;
		if(result.containsKey(len)){
			container = result.get(len);
		}else{
			container = new Vector<long[]>();
			result.put(len, container);
		}
		container.add(path);
		synchronized (resultMutex) {
			resultMutex.notifyAll();
		}
	}
	
	public Map<Integer, Vector<long[]>> getResults() {
		return result;
	}

	public int getNeededResultLength() {
		return neededResultLength;
	}

	public void setNeededResultLength(int neededResultLength) {
		if(neededResultLength>this.neededResultLength){
			synchronized (needMutex) {
				this.neededResultLength = neededResultLength;
				needMutex.notifyAll();
			}
		}
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete() {
		synchronized (resultMutex) {
			this.complete = true;
			resultMutex.notifyAll();
		}
	}

	public QueryContext getContext() {
		return context;
	}

	public BSPRunner getRunner() {
		return runner;
	}
	
	
}
