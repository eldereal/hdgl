package hdgl.db.query.condition;

public final class NoRestriction extends AbstractCondition {

	public static final NoRestriction I = new NoRestriction();
	
	@Override
	public boolean require(AbstractCondition other) {
		return other instanceof NoRestriction;
	}

	@Override
	public int hashCode() {
		return NoRestriction.class.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj!=null&&obj instanceof NoRestriction;
	}

	@Override
	public boolean compatible(AbstractCondition other) {
		return true;
	}

	@Override
	public String toString() {
		return "*";
	}
}
