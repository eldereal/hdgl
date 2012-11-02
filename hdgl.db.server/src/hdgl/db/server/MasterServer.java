package hdgl.db.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;

import hdgl.db.exception.RedirectException;
import hdgl.db.server.protocol.ClientMasterProtocol;
import hdgl.db.server.protocol.ClientRegionProtocol;
import hdgl.db.server.protocol.InetSocketAddressWritable;
import hdgl.db.server.protocol.RegionMasterProtocol;


public class MasterServer implements RegionMasterProtocol, ClientMasterProtocol {

	Configuration conf;
	
	String host;
	int port;
	ArrayList<InetSocketAddress> regions = new ArrayList<InetSocketAddress>();
	
	public MasterServer(String host, int port, Configuration conf){
		this.conf = conf;
		this.host = host;
		this.port = port;
	}

	public void start(){
		
	}
	
	public void stop(){
		
	}

	@Override
	public int regionStart(String host, int port) {
		synchronized (regions) {
			InetSocketAddress address = new InetSocketAddress(host, port);
			int i = regions.indexOf(address);
			if(i<0){
				regions.add(address);
				i = regions.size() - 1;
			}
			return i;
		}
	}

	@Override
	public void regionStop(String host, int port) {
		synchronized (regions) {
			regions.remove(new InetSocketAddress(host, port));
		}		
	}

	@Override
	public InetSocketAddressWritable[] getRegions() {
		InetSocketAddressWritable[] res = new InetSocketAddressWritable[regions.size()];
		for(int i=0;i<res.length;i++){
			res[i] = new InetSocketAddressWritable(regions.get(i));
		}
		return res;
	}
	
}
