package hdgl.db.protocol;

import hdgl.db.exception.BadQueryException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.ipc.ProtocolInfo;

@ProtocolInfo(protocolName = "ClientMasterProtocol", protocolVersion=1)
public interface ClientMasterProtocol{

	public MapWritable getRegions();
	
	/**
	 * Find best places for given entity
	 * @param id
	 * @return an array contains region id
	 */
	public IntWritable[] findEntity(long id);
	
	public int prepareQuery(String query) throws BadQueryException;
	
	/**
	 * start a query 
	 * @param queryId
	 * @return a group of region ids 
	 */
	public IntWritable[] query(int queryId);
}
