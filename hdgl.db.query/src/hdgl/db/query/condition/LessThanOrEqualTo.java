package hdgl.db.query.condition;

public class LessThanOrEqualTo extends BinaryCondition {

	public static final byte FLAG_BYTE=-6;
	
	@Override
	byte getFlagByte() {
		return FLAG_BYTE;
	}
	
	public LessThanOrEqualTo() {
		// TODO Auto-generated constructor stub
	}
	
	public LessThanOrEqualTo(String label, AbstractValue value) {
		super(label, value);
	}
	
	@Override
	public String toString() {
		return getLabel()+"<="+getValue();
	}

	@Override
	public boolean require(AbstractCondition other) {
		if(other instanceof NoRestriction){
			return true;
		}else if(other instanceof BinaryCondition){
			if(other instanceof LessThan){
				return ((LessThan) other).getValue().largerThan(getValue());
			}else if(other instanceof LessThanOrEqualTo){
				return ((LessThanOrEqualTo) other).getValue().largerThanOrEqualTo(getValue());
			}else if(other instanceof NotEqualTo){
				return ((NotEqualTo) other).getValue().largerThan(getValue());
			}else{
				return false;
			}
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
			return getValue().largerThanOrEqualTo(((EqualTo) other).getValue());
		}else if(other instanceof LargerThan){
			return getValue().largerThan(((LargerThan) other).getValue());
		}else if(other instanceof LargerThanOrEqualTo){
			return getValue().largerThanOrEqualTo(((LargerThanOrEqualTo) other).getValue());
		}else{
			return true;
		}
	}
}
