package hdgl.db.store;

public class Common {
	public static String fillToLength(int number)
	{
		String str = String.format("%05d", number);
		return str;
	}
}
