package hdgl.db.impl.memory;

import java.util.Arrays;

public class Tuple {

	private Object[] values;
	
	public Tuple(Object... values){
		this.values = values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tuple))
			return false;
		Tuple other = (Tuple) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}
	
	public Object get(int index){
		return values[index];
	}
	
}
