package hdgl.db.query.condition;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatNumberValue extends AbstractValue {

	public static final byte FLAG_BYTE=-21;
	
	private double value;

	public double getValue() {
		return value;
	}

	public FloatNumberValue(double value) {
		this.value = value;
	}
	
	public FloatNumberValue(String string) {
		this.value = Double.parseDouble(string);
	}

	public FloatNumberValue() {
		// TODO Auto-generated constructor stub
	}

	public static FloatNumberValue parse(String value) {
		return new FloatNumberValue(Double.parseDouble(value));
	}
	
	@Override
	public String toString() {
		return Double.toString(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		FloatNumberValue other = (FloatNumberValue) obj;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
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

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeByte(FLAG_BYTE);
		arg0.writeDouble(value);
	}

	@Override
	public void readTail(DataInput in) throws IOException {
		value = in.readDouble();
	}
}
