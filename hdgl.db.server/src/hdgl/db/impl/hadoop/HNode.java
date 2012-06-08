package hdgl.db.impl.hadoop;

import hdgl.db.graph.HdglException.NodeIdMustBePositiveException;
import hdgl.db.graph.HdglException;
import hdgl.db.graph.Node;

public class HNode extends HLabelContainer implements Node {

	public HNode(int id) throws NodeIdMustBePositiveException {
		super(id);		
		if(id<=0){
			throw new HdglException.NodeIdMustBePositiveException();
		}
	}
	
}
