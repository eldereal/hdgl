package hdgl.db.server.protocol;

import org.apache.hadoop.ipc.ProtocolInfo;

@ProtocolInfo(protocolName="RegionMasterProtocol", protocolVersion=1)
public interface RegionMasterProtocol {

	public int regionStart(String host, int port);
	
	public void regionStop(String host, int port);
}
