package hdgl.db.query.condition;

public class NotEqualTo extends BinaryCondition {

	public static final byte FLAG_BYTE=-8;
	
	@Override
	byte getFlagByte() {
		return FLAG_BYTE;
	}
	
	public NotEqualTo() {
	}
	
	public NotEqualTo(String label, AbstractValue value) {
		super(label, value);
	}

	@Override
	public String toString() {
		return getLabel()+"<>"+getValue();
	}

	@Override
	public boolean require(AbstractCondition other) {
		if(other instanceof NoRestriction){
			return true;
		}else if(other.equals(this)){
			return true;
//		}else if(other instanceof Conjunction){
//			for (AbstractCondition condition : ((Conjunction) other).getConditions()) {
//				if(!require(condition)){
//					return false;
//				}
//			}
//			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean compatible(AbstractCondition other) {
		if(other instanceof EqualTo){
			return !getValue().equals(((EqualTo) other).getValue());
		}else{
			return true;
		}
	}
}
