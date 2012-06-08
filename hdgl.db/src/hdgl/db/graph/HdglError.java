package hdgl.db.graph;

public class HdglError extends Error {

	

	public HdglError() {
		super();
		
	}

	public HdglError(String message, Throwable cause) {
		super(message, cause);
		
	}

	public HdglError(String message) {
		super(message);
		
	}

	public HdglError(Throwable cause) {
		super(cause);
		
	}

	public static class AssertionError extends HdglError{
		
		public AssertionError(String assertion, Throwable cause) {
			super(assertion, cause);
		}
		
	}
	
}
