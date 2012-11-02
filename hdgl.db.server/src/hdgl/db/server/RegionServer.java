package hdgl.db.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import hdgl.db.conf.MasterConf;
import hdgl.db.server.protocol.ClientRegionProtocol;
import hdgl.db.server.protocol.RegionMasterProtocol;


public class RegionServer implements ClientRegionProtocol {

	Configuration conf;
	RegionMasterProtocol master;
	String host;
	int port;
	int regionId;
	
	public RegionServer(String host, int port, Configuration conf){
		this.host = host;
		this.port = port;
		this.conf = conf;
	}

	@Override
	public String echo(String value) {
		return value;
	}

	@Override
	public byte[] getEntity(long id) {
		return null;
	}
	
	public void stop(){
		master.regionStop(host, port);
	}

	public void start() throws IOException {
		final String mhost = MasterConf.getMasterHost(conf);
		final int mport = MasterConf.getRegionMasterPort(conf);
		master = RPC.getProxy(RegionMasterProtocol.class, 1, new InetSocketAddress(mhost, mport), conf);
		regionId = master.regionStart(host, port);
	}
	
}
