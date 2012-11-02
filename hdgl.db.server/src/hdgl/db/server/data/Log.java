package hdgl.db.server.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class Log implements Writable {
	
	static final byte ADD_VERTEX = 0;
	static final byte ADD_EDGE = 1;
	static final byte SET_LABEL = 2;
	static final byte DELETE_ENTITY = 3;
	static final byte DELETE_LABEL = 4;
	
	public static Log addVertex(long tempId, String oftype){
		return new Log(ADD_VERTEX, tempId, 0, 0, oftype, null);
	}
	
	public static Log addEdge(long tempId, String oftype, long v1, long v2){
		return new Log(ADD_EDGE, tempId, v1, v2, oftype, null);
	}
	
	public static Log setLabel(long entity, String name, byte[] value){
		return new Log(SET_LABEL, entity, 0, 0, null, value);
	}
	
	byte type;
	long id1,id2,id3;
	String name;
	byte[] data;
	
	public Log(){
		
	}
	
	public Log(byte type, long id1, long id2,long id3, String name, byte[] data) {
		super();
		this.type = type;
		this.id1 = id1;
		this.id2 = id2;
		this.name = name;
		this.data = data;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		type = in.readByte();
		switch (type) {
		case ADD_VERTEX:
			id1 = in.readLong();
			name = in.readUTF();
			break;
		case ADD_EDGE:
			id1 = in.readLong();
			id2 = in.readLong();
			id3 = in.readLong();
			name = in.readUTF();
		case SET_LABEL:
			id1 = in.readLong();
			name = in.readUTF();
			int len=in.readInt();
			data = new byte[len];
			in.readFully(data);
		default:
			throw new IllegalArgumentException("Illegal log type: "+ type);
		}
	}
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(type);
		switch (type) {
		case ADD_VERTEX:
			out.writeLong(id1);
			out.writeUTF(name);
			break;
		case ADD_EDGE:
			out.writeLong(id1);
			out.writeLong(id2);
			out.writeLong(id3);
			out.writeUTF(name);			
		case SET_LABEL:
			out.writeLong(id1);
			out.writeUTF(name);
			out.writeInt(data.length);
			out.write(data);			
		default:
			throw new IllegalArgumentException("Illegal log type: "+ type);
		}
		
	}
	
	
}
