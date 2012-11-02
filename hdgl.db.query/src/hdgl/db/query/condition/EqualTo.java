package hdgl.db.query.condition;

public class EqualTo extends UniaryCondition {

	public EqualTo(String label, AbstractValue value) {
		super(label, value);		
	}

	@Override
	public String toString() {
		return getLabel()+"="+getValue();
	}

	@Override
	public boolean require(AbstractCondition other) {
		if(other instanceof NoRestriction){
			return true;
		}else if(other instanceof UniaryCondition){
			if (other instanceof LessThan){		
				return ((LessThan) other).getValue().largerThan(getValue());
			}else if(other instanceof LessThanOrEqualTo){
				return ((LessThanOrEqualTo) other).getValue().largerThanOrEqualTo(getValue());
			}else if(other instanceof LargerThan){
				return ((LargerThan) other).getValue().lessThan(getValue());
			}else if(other instanceof LargerThanOrEqualTo){
				return ((LargerThanOrEqualTo) other).getValue().lessThanOrEqualTo(getValue());
			}else if(other instanceof EqualTo){
				return ((EqualTo) other).getValue().equals(getValue());
			}else if(other instanceof NotEqualTo){
				return !((NotEqualTo) other).getValue().equals(getValue());
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
			return equals(other);
		}else if(other instanceof NotEqualTo){
			return !getValue().equals(((NotEqualTo) other).getValue());
		}else if(other instanceof LargerThan){
			return getValue().largerThan(((LargerThan) other).getValue());
		}else if(other instanceof LargerThanOrEqualTo){
			return getValue().largerThanOrEqualTo(((LargerThanOrEqualTo) other).getValue());
		}else if(other instanceof LessThan){
			return getValue().lessThan(((LessThan) other).getValue());
		}else if(other instanceof LessThanOrEqualTo){
			return getValue().lessThanOrEqualTo(((LessThanOrEqualTo) other).getValue());
		}else{
			return true;
		}
	}
	
}
