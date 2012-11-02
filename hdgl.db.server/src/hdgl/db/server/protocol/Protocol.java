package hdgl.db.server.protocol;

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
	
}
