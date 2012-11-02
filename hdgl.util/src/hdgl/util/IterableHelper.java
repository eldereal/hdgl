package hdgl.util;

import java.util.Collection;
import java.util.Random;


public class IterableHelper{

	public static <T> T randomSelect(Collection<T> collection){
		if(collection.isEmpty()){
			return null;
		}
		double count = collection.size();
		Random random = new Random();
		for(T t:collection){
			if(random.nextDouble()<1/count){
				return t;
			}
			count--;
		}
		throw new RuntimeException("Should never run to here");
	}
	
}
