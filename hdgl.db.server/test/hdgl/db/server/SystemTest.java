package hdgl.db.server;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import hdgl.db.conf.GraphConf;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.ClientRegionProtocol;
import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.db.protocol.Protocol;
import hdgl.util.NetHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;
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
		assertEquals(1, master.getRegions().entrySet().size());
	}
	
	@Test
	public void forRegion() throws Exception {
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
		MapWritable regions = master.getRegions();
		int count=0;
		for(Writable region:regions.values()){
			InetSocketAddress addr = ((InetSocketAddressWritable)region).toAddress();
			ClientRegionProtocol r = Protocol.region(addr, conf);
			assertEquals("abcde", r.echo("abcde"));
			count++;
		}
		assertTrue("at least one region is tested", count>0);
	}
	
	@Test
	public void queryTest() throws Exception{
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
		int queryId = master.prepareQuery(".[id=1]|-[price<10](.)*");
		System.out.println("query id: "+queryId);
	}

}
