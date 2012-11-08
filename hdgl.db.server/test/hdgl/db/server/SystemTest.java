package hdgl.db.server;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import hdgl.db.conf.GraphConf;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.RegionProtocol;
import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.db.protocol.Protocol;
import hdgl.util.NetHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
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
		for(Entry<Writable, Writable> v : master.getRegions().entrySet()){
			System.out.println("region "+v.getKey()+" - "+v.getValue());
		}
	}
	
	@Test
	public void forRegion() throws Exception {
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
		MapWritable regions = master.getRegions();
		int count=0;
		for(Writable region:regions.values()){
			InetSocketAddress addr = ((InetSocketAddressWritable)region).toAddress();
			RegionProtocol r = Protocol.region(addr, conf);
			assertEquals("abcde", r.echo("abcde"));
			count++;
		}
		assertTrue("at least one region is tested", count>0);
	}
	
	@Test
	public void queryTest() throws Exception{
		Configuration conf = GraphConf.getDefault();
		ClientMasterProtocol master = Protocol.master(conf);
		int queryId = master.prepareQuery(".(-.)+");
		System.out.println("query id: "+queryId);
		IntWritable[] regionIds=master.query(queryId);
		MapWritable regions = master.getRegions();
		ArrayList<RegionProtocol> executeRegionConns=new ArrayList<RegionProtocol>();
		for(IntWritable regionId:regionIds){
			System.out.println("execute region: "+regionId.get()+" - "+regions.get(regionId));
			InetSocketAddress regionAddress=((InetSocketAddressWritable)regions.get(regionId)).toAddress();
			executeRegionConns.add(Protocol.region(regionAddress, conf));
		}
		
		for(RegionProtocol r:executeRegionConns){
			r.doQuery(queryId, 0);
		}
		for(RegionProtocol r:executeRegionConns){
			long[][] res = r.fetchResult(queryId, 1);
			System.out.println("result len 1:" + Arrays.toString());
		}
		for(RegionProtocol r:executeRegionConns){
			System.out.println("result len 2:" + Arrays.toString(r.fetchResult(queryId, 2)));
		}
		for(RegionProtocol r:executeRegionConns){
			r.fetchResult(queryId, 3);
		}
	}

}
