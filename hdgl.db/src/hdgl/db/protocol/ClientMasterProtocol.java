package hdgl.db.protocol;

import hdgl.db.exception.BadQueryException;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.ipc.ProtocolInfo;

@ProtocolInfo(protocolName = "ClientMasterProtocol", protocolVersion=1)
public interface ClientMasterProtocol{

	public MapWritable getRegions();
	
	public InetSocketAddressWritable findEntity(long id);
	
	public int prepareQuery(String query) throws BadQueryException;
	
	public InetSocketAddressWritable[] query(int queryId);
}
