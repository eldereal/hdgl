package hdgl.db.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.synth.Region;

import hdgl.db.conf.MasterConf;
import hdgl.db.conf.RegionConf;
import hdgl.db.protocol.ClientMasterProtocol;
import hdgl.db.protocol.ClientRegionProtocol;
import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.util.IterableHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.ipc.RPC;

public class HConn {

	Configuration conf;
	ClientMasterProtocol masterProtocol;
	Map<Integer, ClientRegionProtocol> regions = new HashMap<Integer, ClientRegionProtocol>();
	MapWritable regionAddrs;
	
	public HConn(Configuration conf){
		this.conf = conf;
	}
	
	public ClientMasterProtocol master() throws IOException{
		if(masterProtocol==null){
			masterProtocol = RPC.getProxy(ClientMasterProtocol.class, 1, new InetSocketAddress(MasterConf.getMasterHost(conf), MasterConf.getClientMasterPort(conf)), conf);
		}
		return masterProtocol;
	}
	
	public ClientRegionProtocol region() throws IOException{
		if(!regions.isEmpty()){
			return regions.get(IterableHelper.randomSelect(regions.keySet()));
		}else{
			if(regionAddrs == null){
				regionAddrs = master().getRegions();
			}
			return region(((IntWritable)IterableHelper.randomSelect(regionAddrs.keySet())).get());
		}
	}
	
	public ClientRegionProtocol region(int regionId) throws IOException{
		if(regions.containsKey(regionId)){
			return regions.get(regionId);
		}
		if(regionAddrs == null){
			regionAddrs = master().getRegions();
		}
		InetSocketAddressWritable addr = (InetSocketAddressWritable) regionAddrs.get(new IntWritable(regionId));
		ClientRegionProtocol region = RPC.getProxy(ClientRegionProtocol.class, 1, addr.toAddress(), conf);
		regions.put(regionId, region);
		return region;		
	}
}
