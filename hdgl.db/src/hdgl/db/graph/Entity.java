package hdgl.db.graph;

import java.util.Map;

public interface Entity {
	
	public long getId();
	public String getType();
	public Iterable<LabelValue> getLabels();
	public byte[] getLabel(String name);
	
}
