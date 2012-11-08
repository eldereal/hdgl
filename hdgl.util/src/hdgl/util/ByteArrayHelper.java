package hdgl.util;

public class ByteArrayHelper {
	
	public static byte[] toBytes(int number){
		byte[] data = new byte[4];
		data[0]=(byte) (number>>>24&0xff);
		data[1]=(byte) (number>>>16&0xff);
		data[2]=(byte) (number>>>8&0xff);
		data[3]=(byte) (number&0xff);
		return data;
	}
	
	public static byte[] toBytes(String str){
		return str.getBytes();
	}
	
	public static int parseInt(byte[] data){
		return (data[0]<<24)|(data[1]<<16)|(data[2]<<8)|(data[3]);
	}
	
	public static String parseString(byte[] data){
		return new String(data);
	}
	
}
