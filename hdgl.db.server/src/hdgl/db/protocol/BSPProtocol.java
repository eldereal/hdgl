package hdgl.db.protocol;

import hdgl.db.query.QueryContext;

import org.apache.hadoop.ipc.ProtocolInfo;


@ProtocolInfo(protocolName = "RegionBSPProtocol", protocolVersion = 1)
public interface BSPProtocol {

	public void initBSP(int querySession, String zkRoot, QueryContext ctx);
	
	public void broadcastMessage(int querySession, MessagePackWritable msg);
	
	public void sendMessage(int querySession, int vertexId, MessagePackWritable msg);
	
}
