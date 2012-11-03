package hdgl.db.query.condition;

public class LargerThanOrEqualTo extends BinaryCondition {
	public static final byte FLAG_BYTE=-4;
	
	@Override
	byte getFlagByte() {
		return FLAG_BYTE;
	}
	
	public LargerThanOrEqualTo() {
	
	}
	
	public LargerThanOrEqualTo(String label, AbstractValue value) {
		super(label, value);
	}
	
	@Override
	public String toString() {
		return getLabel()+">="+getValue();
	}

	@Override
	public boolean require(AbstractCondition other) {
		if(other instanceof NoRestriction){
			return true;
		}else if(other instanceof BinaryCondition){
			if(other instanceof LargerThan){
				return ((LargerThan) other).getValue().lessThan(getValue());
			}else if(other instanceof LargerThanOrEqualTo){
				return ((LargerThanOrEqualTo) other).getValue().lessThanOrEqualTo(getValue());
			}else if(other instanceof NotEqualTo){
				return ((NotEqualTo) other).getValue().lessThan(getValue());
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
			return getValue().lessThanOrEqualTo(((EqualTo) other).getValue());
		}else if(other instanceof LessThan){
			return getValue().lessThan(((LessThan) other).getValue());
		}else if(other instanceof LessThanOrEqualTo){
			return getValue().lessThanOrEqualTo(((LessThanOrEqualTo) other).getValue());
		}else{
			return true;
		}
	}
}
