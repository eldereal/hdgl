package hdgl.db.graph.query;

import java.util.HashMap;
import java.util.Map;

public class Op {

	public static String EQ = "=";
	public static String NEQ = "<>";
	public static String LT = "<";
	public static String LTEQ = "<=";
	public static String GT = ">";
	public static String GTEQ = ">=";
	
	private static Map<String, String> notmap;
	
	static{
		notmap = new HashMap<String, String>();
		notmap.put(EQ, NEQ);
		notmap.put(NEQ, EQ);
		notmap.put(GT, LTEQ);
		notmap.put(LT, GTEQ);
		notmap.put(GTEQ, LT);
		notmap.put(LTEQ, GT);
	}
	
	private Op(){
		
	}
	
	public static String not(String op){
		return notmap.get(op);
	}
	
}
