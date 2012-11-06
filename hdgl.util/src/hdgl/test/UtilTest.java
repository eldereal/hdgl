package hdgl.test;

import static org.junit.Assert.*;
import hdgl.util.NetHelper;

import org.junit.Test;

public class UtilTest {

	@Test
	public void test() {
		System.out.println(NetHelper.getMyHostName());
	}

}
