package hdgl.db.query.condition;

public abstract class AbstractValue {

	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
	public abstract boolean lessThan(AbstractValue obj);
	
	public abstract boolean largerThan(AbstractValue obj);
	
	public boolean lessThanOrEqualTo(AbstractValue value){
		return lessThan(value)||equals(value);
	}
	
	public boolean largerThanOrEqualTo(AbstractValue value){
		return largerThan(value)||equals(value);
	}
	
}
