package hdgl.db.server.protocol;

import hdgl.db.server.data.Log;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.ProtocolInfo;


@ProtocolInfo(protocolName = "ClientRegionProtocol", protocolVersion = 1)
public interface ClientRegionProtocol{

	public String echo(String value);
	
	public byte[] getEntity(long id);
	
	public InetSocketAddressWritable[] doQuery(int queryId);
	
	public long[][] fetchResult(int queryId);
	
	public int beginTx();
	
	public void writeLog(int txId, Log log);
	
	public int commit(int txId);
	
	public int abort(int txId);
	
	public boolean txTaskStatus(int txId, int waitMilliseconds);
	
	public boolean txTaskResult(int txId);
}
