package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;
import hdgl.util.IterableHelper;

public class HVertex implements Vertex {

	long id;
	String type;
	Map<String, byte[]> labelsMap = new HashMap<String, byte[]>();
	ArrayList<Long> inlist = new ArrayList<Long>();
	ArrayList<Long> outlist = new ArrayList<Long>();
	GraphStore store;
	
	public HVertex(long id, String type, GraphStore store){
		assert id>0;
		this.id = id;
		this.type = type;
		this.store = store;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void addInEdge(long edgeId, long anotherVertex)
	{
		inlist.add(edgeId);
	}
	
	public void addOutEdge(long edgeId, long anotherVertex)
	{
		outlist.add(edgeId);
	}
	
	public void addLabel(String key, byte[] value)
	{
		labelsMap.put(key, value);
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Iterable<LabelValue> getLabels() {
		return  IterableHelper.select(labelsMap.entrySet(), new IterableHelper.Map<Map.Entry<String,byte[]>, LabelValue>() {

			@Override
			public LabelValue select(final Entry<String, byte[]> element) {
				return new LabelValue() {
					
					@Override
					public byte[] getValue() {
						return element.getValue();
					}
					
					@Override
					public String getName() {
						return element.getKey();
					}
				};
			}

		});
	}

	@Override
	public Iterable<Edge> getOutEdges() {
		return IterableHelper.select(outlist, new IterableHelper.Map<Long, Edge>(){
			@Override
			public Edge select(Long element) {
				try {
					return store.parseEdge(element);
				} catch (IOException e) {
					throw new HdglException(e);
				}
			}
		});
	}

	@Override
	public Iterable<Edge> getInEdges() {
		return IterableHelper.select(inlist, new IterableHelper.Map<Long, Edge>(){
			@Override
			public Edge select(Long element) {
				try {
					return store.parseEdge(element);
				} catch (IOException e) {
					throw new HdglException(e);
				}
			}
		});
	}

	@Override
	public Iterable<Edge> getEdges() {
		return IterableHelper.concat(getOutEdges(), getInEdges());
	}

	@Override
	public byte[] getLabel(String name) {
		return labelsMap.get(name);
	}

}
