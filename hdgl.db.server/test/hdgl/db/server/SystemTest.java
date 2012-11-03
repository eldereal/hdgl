package hdgl.db.server;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import hdgl.db.conf.GraphConf;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.ClientRegionProtocol;
import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.db.protocol.Protocol;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SystemTest {

	static HGRegion region;
	static HGMaster master;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration conf = GraphConf.getDefault();
		master = new HGMaster(conf);
		master.start();
		region = new HGRegion(conf);
		region.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		region.stop();
		master.stop();
	}

	@Test
	public void forMaster() throws Exception {
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
		System.out.println(master.getRegions().entrySet());
	}
	
	@Test
	public void forRegion() throws Exception {
		Configuration conf = GraphConf.getDefault();
		ClientRegionProtocol region = RPC.getProxy(ClientRegionProtocol.class, 1, new InetSocketAddress("localhost", 5367), conf);
		assertEquals("abcde", region.echo("abcde"));
	}

}
