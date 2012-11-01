package hdgl.db.server.region;

import org.apache.hadoop.conf.Configuration;

import hdgl.db.protocol.ClientRegionProtocol;


public class RegionServer implements ClientRegionProtocol {

	Configuration conf;
	
	static{
		
	}
	
	public RegionServer(Configuration conf){
		this.conf = conf;
	}

	@Override
	public String echo(String value) {
		return value;
	}
	
}
