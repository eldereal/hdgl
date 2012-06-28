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
	
	public static class CannotQueryNewEntity extends HdglError{
		
		public CannotQueryNewEntity(){
			super("Cannot execute query on a new created entity before commit it");
		}
		
	}
	
	public static class LabelNotExistError extends HdglError{
		public LabelNotExistError(String labelname, LabelContainer container){
			super("Label "+labelname+" doesn't exists in "+container);
		}
	}
	

	public static class NodeIdsOverflowError extends HdglError{
		
		public NodeIdsOverflowError(){
			super("Reached maximum node count");
		}
		
	}
	
	public static class RelationshipIdsOverflowError extends HdglError{
		
		public RelationshipIdsOverflowError(){
			super("Reached maximum relationship count");
		}
	}
	
	public static class IllegalTypeError extends HdglError{
		
		public IllegalTypeError(String param, String type, String realType){
			super(param+" should be "+type+ ", but is "+realType);
		}
	}
	
	public static class MaximumInheritLevelError extends HdglError{
		public MaximumInheritLevelError(){
			super("NodeType or RelationshipType's inherit level must lesser than 8");
		}
	}
	
	public static class MaximumSubclassesOfTypeError extends HdglError{
		public MaximumSubclassesOfTypeError(String typename, int limit){
			super(typename + "'s subclasses should less than "+limit);
		}
	}
	
	public static class AssertionError extends HdglError{
		
		public AssertionError(String assertion, Throwable cause) {
			super(assertion, cause);
		}
		
	}
	
}
