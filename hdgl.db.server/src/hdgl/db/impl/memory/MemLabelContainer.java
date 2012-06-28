package hdgl.db.impl.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Callable;


import hdgl.db.graph.HdglError;
import hdgl.db.graph.LabelContainer;
import hdgl.db.task.AsyncResult;
import hdgl.db.task.CallableAsyncResult;

public class MemLabelContainer implements LabelContainer {

	private static final Object NULL = new Object();
	
	private LabelContainer base;
	private HashMap<String, Long> timestamps = new HashMap<String, Long>();
	private HashMap<String, Object> values = new HashMap<String, Object>(); 
	
	public MemLabelContainer(LabelContainer base) {
		this.base = base;
	}

	public MemLabelContainer(){
		
	}	
	
	@Override
	public int getId() {
		if(base==null){
			throw new RuntimeException("Memcached entities doesn't have an unique id");
		}
		return base.getId();
	}	

	@Override
	public long getTimestamp(String name) {
		if(timestamps.containsKey(name)){
			return timestamps.get(name);
		}else if(base!=null){
			return base.getTimestamp(name);
		}else{
			throw new HdglError.LabelNotExistError(name, this);
		}
	}

	@Override
	public Object getLabel(String name) {
		if(values.containsKey(name)){
			Object val = values.get(name);
			if(val==NULL){
				return null;
			}
			return val;
		}else if(base!=null){
			return base.getLabel(name);
		}else{
			return null;
		}
	}

	@Override
	public <T> T getLabel(String name, Class<T> type) {
		return type.cast(getLabel(name));
	}
	
	public void setLabel(String name,Object value,long timestamp){
		values.put(name, value);
		timestamps.put(name,timestamp);
	}
	
	public void removeLabel(String name,long timestamp){
		values.put(name,NULL);
		timestamps.put(name,timestamp);
	}

	@Override
	public AsyncResult<Iterable<Long>> getHistoricalTimestamps(final String name)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public AsyncResult<Object> getHistoricalLabel(String name, long timestamp)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> AsyncResult<T> getHistoricalLabel(String name, long timestamp,
			Class<T> type) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
