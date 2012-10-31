package hdgl.db.query.condition;

import java.util.Arrays;

public class Conjunction extends AbstractCondition {

	AbstractCondition[] conditions;

	public Conjunction(AbstractCondition[] conditions) {
		super();
		this.conditions = conditions;
	}

	public AbstractCondition[] getConditions() {
		return conditions;
	}

	public void setConditions(AbstractCondition[] conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean require(AbstractCondition other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean compatible(AbstractCondition other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(conditions);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conjunction other = (Conjunction) obj;
		if (!Arrays.equals(conditions, other.conditions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(conditions);
	}
	
}
