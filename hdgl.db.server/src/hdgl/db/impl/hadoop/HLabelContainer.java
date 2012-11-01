package hdgl.db.impl.hadoop;

import hdgl.db.graph.deprecated.LabelContainer;
import hdgl.db.task.AsyncResult;

public class HLabelContainer implements LabelContainer {

	protected int id;
	
	public HLabelContainer(int id){
		this.id=id;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public long getTimestamp(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getLabel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getLabel(String name, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncResult<Iterable<Long>> getHistoricalTimestamps(String name)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncResult<Object> getHistoricalLabel(String name, long timestamp)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> AsyncResult<T> getHistoricalLabel(String name, long timestamp,
			Class<T> type) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

}
