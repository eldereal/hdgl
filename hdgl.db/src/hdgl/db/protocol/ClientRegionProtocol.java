package hdgl.db.protocol;

import org.apache.hadoop.ipc.ProtocolInfo;


@ProtocolInfo(protocolName = "ClientRegionProtocol", protocolVersion = 1)
public interface ClientRegionProtocol {

	public String echo(String value);
	
	public byte[] getEntityHead(long id);
	
	public byte[] getEntityTail(long id);
	
}
