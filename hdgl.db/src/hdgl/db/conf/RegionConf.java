package hdgl.db.conf;

import org.apache.hadoop.conf.Configuration;

public final class RegionConf {
	
	public static final String REGION_HOST="hdgl.region.host";
	public static final String REGION_PORT="hdgl.region.port";
	
	public class Defaults{
		public static final String REGION_HOST = "localhost";
		public static final int REGION_PORT = 5367;
	}
	
	public static String getRegionServerHost(Configuration conf) {
		return conf.get(REGION_HOST, Defaults.REGION_HOST);
	}
	
	public static int getRegionServerPort(Configuration conf) {
		return conf.getInt(REGION_PORT, Defaults.REGION_PORT);
	}
	
}
