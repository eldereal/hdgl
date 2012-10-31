package hdgl.db.query.condition;

public class IntNumberValue extends AbstractValue {

	private int value;

	public int getValue() {
		return value;
	}

	public IntNumberValue(int value) {
		this.value = value;
	}
	
	public IntNumberValue(String string) {
		this.value = Integer.parseInt(string);
	}

	public static IntNumberValue parse(String value) {
		return new IntNumberValue(Integer.parseInt(value));
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
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
		IntNumberValue other = (IntNumberValue) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public boolean lessThan(AbstractValue obj) {
		if(obj instanceof IntNumberValue){
			return value<((IntNumberValue)obj).getValue();
		}else if(obj instanceof FloatNumberValue){
			return value<((FloatNumberValue)obj).getValue();
		}else{
			throw new ArithmeticException("Not comparable");
		}
	}

	@Override
	public boolean largerThan(AbstractValue obj) {
		if(obj instanceof IntNumberValue){
			return value>((IntNumberValue)obj).getValue();
		}else if(obj instanceof FloatNumberValue){
			return value>((FloatNumberValue)obj).getValue();
		}else{
			throw new ArithmeticException("Not comparable");
		}
	}
}
