package hdgl.db.server.bsp;

import hdgl.db.protocol.MessagePackWritable;

public interface BSPContainer {

	public void sendMessagePack(int sessionId, int regionId, MessagePackWritable pack);
	
	public void superStepFinish(int sessionId, int superstep);
}
