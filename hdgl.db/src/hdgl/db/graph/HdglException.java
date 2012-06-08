package hdgl.db.graph;

import java.io.IOException;

public class HdglException extends IOException {

	public static class DataIsNotWritableOrSerializableException extends HdglException{
		public DataIsNotWritableOrSerializableException(){
			super("Data must be Writable or Serializable to serialize");
		}
	}
	
	public static class NodeIdMustBePositiveException extends HdglException{
		public NodeIdMustBePositiveException(){
			super("Id for a node must greater than zero");
		}
	}
	
	public static class RelationshipIdMustBeNegativeException extends HdglException{
		public RelationshipIdMustBeNegativeException(){
			super("Id for a relationship must lesser than zero");
		}
	}
	
	public HdglException() {
		
	}
	
	public HdglException(String message, Throwable cause) {
		super(message, cause);		
	}

	public HdglException(String message) {
		super(message);		
	}

	public HdglException(Throwable cause){
		super(cause);
	}
	
}
