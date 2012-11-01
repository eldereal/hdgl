package hdgl.db.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Pattern;

import hdgl.db.impl.hadoop.HConf;
import hdgl.db.impl.hadoop.HGraph;
import hdgl.db.protocol.ClientRegionProtocol;
import hdgl.db.server.region.RegionServer;
import hdgl.util.ParameterHelper;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;


public class HGRegion {

	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(HGraph.class);
	
	/**
	 * 分析一个形如host:port的字符串，其中host与port部分都可以为空
	 * @return
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	private static InetSocketAddress parseHostPort(String str, int defaultPort) throws NumberFormatException, UnknownHostException{
		if(str.contains(":")){
			String[] parts=str.split(":", 2);
			return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
		}else{
			if(Pattern.matches("\\d+", str)){
				return new InetSocketAddress(InetAddress.getLocalHost(), Integer.parseInt(str));
			}else{
				return new InetSocketAddress(str,defaultPort);
			}
		}
	}
	
	RegionServer regionServer;
	Server server;
	Configuration configuration;
	
	public HGRegion(Configuration conf) {
		this.configuration = conf;
	}
	
	public void start() throws IOException{
		String host=HConf.getRegionServerHost(configuration);
		int port = HConf.getRegionServerPort(configuration);
		Log.info("Starting HGRegion at " + host+":" + port);
		regionServer = new RegionServer(configuration);
		server = RPC.getServer(ClientRegionProtocol.class, regionServer, host, port, configuration);
		server.start();
	}
	
	public void stop(){
		if(server!=null){
			server.stop();
		}
	}
	
	public static void main(String[] args) throws IOException {
		HGRegion region = new HGRegion(new Configuration());
		region.start();
	}
	
}
