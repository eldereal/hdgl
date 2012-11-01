package hdgl.db.server;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import hdgl.db.protocol.ClientRegionProtocol;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RegionTest {

	static HGRegion region;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration conf = new Configuration();
		region = new HGRegion(conf);
		region.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		region.stop();		
	}

	@Test
	public void test() throws Exception {
		Configuration conf = new Configuration();
		ClientRegionProtocol region = RPC.getProxy(ClientRegionProtocol.class, 1, new InetSocketAddress("localhost", 5367), conf);
		assertEquals("abcde", region.echo("abcde"));
	}

}
