package hdgl.db.query.condition;

public class StringValue extends AbstractValue {

	private String value;

	public StringValue(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static StringValue parse(String value){
		return new StringValue(value);
	}
	
	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringValue other = (StringValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public boolean lessThan(AbstractValue obj) {
		throw new ArithmeticException("Not comparable");
	}

	@Override
	public boolean largerThan(AbstractValue obj) {
		throw new ArithmeticException("Not comparable");
	}
}
