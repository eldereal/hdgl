package hdgl.db.query;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Writable;

import hdgl.db.protocol.InetSocketAddressWritable;
import hdgl.db.query.stm.StateMachine;

public class QueryContext implements Writable {

	StateMachine stm;
	Map<Long, InetSocketAddressWritable> idMap = new HashMap<Long, InetSocketAddressWritable>();
	String zkRoot;
	
	
	public String getZkRoot() {
		return zkRoot;
	}

	public void setZkRoot(String zkRoot) {
		this.zkRoot = zkRoot;
	}

	public void put(Long blockId, InetSocketAddressWritable addr){
		idMap.put(blockId, addr);
	}

	public StateMachine getStateMachine() {
		return stm;
	}

	public void setStateMachine(StateMachine stm) {
		this.stm = stm;
	}

	public Map<Long, InetSocketAddressWritable> getIdMap() {
		return idMap;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		stm = new StateMachine();
		stm.readFields(in);
		zkRoot = in.readUTF();
		int len = in.readInt();
		idMap.clear();
		for(int i = 0;i<len;i++){
			long id = in.readLong();
			InetSocketAddressWritable addr=new InetSocketAddressWritable();
			addr.readFields(in);
			idMap.put(id, addr);
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		stm.write(out);
		out.writeUTF(zkRoot);
		Set<Map.Entry<Long, InetSocketAddressWritable>> idset = idMap.entrySet();
		out.writeInt(idset.size());
		for(Map.Entry<Long, InetSocketAddressWritable> pair:idset){
			out.writeLong(pair.getKey());
			pair.getValue().write(out);
		}
	}
	
	
	
}
