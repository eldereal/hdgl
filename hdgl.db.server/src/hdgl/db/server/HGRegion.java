package hdgl.db.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Pattern;

import hdgl.db.impl.hadoop.HConf;
import hdgl.db.impl.hadoop.HGraph;
import hdgl.util.ParameterHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

public class HGRegion {

	private static Logger Log = Logger.getLogger(HGraph.class);
	
	/**
	 * 分析一个形如host:port的字符串，其中host与port部分都可以为空
	 * @return
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	private static SocketAddress parseHostPort(String str, int defaultPort) throws NumberFormatException, UnknownHostException{
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
	
	public static void main(String[] args) throws Exception {
		InetAddress localhost = InetAddress.getLocalHost();
		Configuration configuration = HConf.getConfiguration();
		Map<String,String[]> parameters = ParameterHelper.parseParameters(args);
		SocketAddress endpoint;
		if(parameters.containsKey("h")&&parameters.get("h").length>0){
			endpoint = parseHostPort(parameters.get("h")[1], HConf.getDefaultRegionServerPort(configuration));
		}else{
			endpoint = new InetSocketAddress(localhost, HConf.getDefaultRegionServerPort(configuration));
		}
		Log.info("HGRegion starts at");
		
	}
	
}
