package hdgl.db.protocol;

import hdgl.db.conf.MasterConf;

import java.io.IOException;
import java.net.InetSocketAddress;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;


public class Protocol{

	public static ClientMasterProtocol master(Configuration conf) throws IOException{
		String host= MasterConf.getMasterHost(conf);
		int port = MasterConf.getClientMasterPort(conf);
		return RPC.getProxy(ClientMasterProtocol.class, 1, new InetSocketAddress(host, port), conf);
	}
	
	public static ClientRegionProtocol region(String host, int port, Configuration conf) throws IOException{
		return RPC.getProxy(ClientRegionProtocol.class, 1, new InetSocketAddress(host, port), conf);
	}
	
	public static ClientRegionProtocol region(InetSocketAddress addr, Configuration conf) throws IOException{
		return RPC.getProxy(ClientRegionProtocol.class, 1, addr, conf);
	}
}
