package hdgl.db.graph;

import java.io.IOException;

public class HdglException extends IOException {

	public static class NodeNotFound extends HdglException{
		public NodeNotFound(){
			super("Cannot find node");
		}
	}
	
	public static class DataIsNotWritableOrSerializableException extends HdglException{
		public DataIsNotWritableOrSerializableException(){
			super("Data must be Writable or Serializable to serialize");
		}
	}
	
	public static class ConcurrentModifyInOneSessionException extends HdglException{
		public ConcurrentModifyInOneSessionException(){
			super("Another MutableGraph is already opend in current session");
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
