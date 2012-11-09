package hdgl.test;

import static org.junit.Assert.*;
import hdgl.util.ByteArrayHelper;
import hdgl.util.NetHelper;

import org.junit.Test;

public class UtilTest {

	@Test
	public void test() {
		System.out.println(NetHelper.getMyHostName());
		assertEquals(13, ByteArrayHelper.parseInt(ByteArrayHelper.toBytes(13)));
		assertEquals("test string", ByteArrayHelper.parseString(ByteArrayHelper.toBytes("test string")));
	}

}
