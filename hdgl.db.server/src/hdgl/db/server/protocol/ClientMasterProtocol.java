package hdgl.db.server.protocol;

import java.net.InetSocketAddress;

import org.apache.hadoop.ipc.ProtocolInfo;

@ProtocolInfo(protocolName = "ClientMasterProtocol", protocolVersion=1)
public interface ClientMasterProtocol{

	public InetSocketAddressWritable[] getRegions();
	
	public InetSocketAddressWritable findEntity(long id);
	
	public int prepareQuery(String query);
	
	public InetSocketAddressWritable[] query(int queryId);
}
