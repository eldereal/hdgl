package hdgl.db.impl.hadoop.logio;

import hdgl.db.graph.HdglException;
import hdgl.db.impl.hadoop.HConf;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.ArrayPrimitiveWritable;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.ShortWritable;
import org.apache.hadoop.io.Writable;

public class Log implements Writable {

	public static final int CREATE_NODE = 0;
	public static final int CREATE_RELATIONSHIP = 1;
	public static final int DELETE_NODE = 3;
	public static final int DELETE_RELATIONSHIP = 4;
	
	public static final int ADD_LABEL = 5;
	public static final int MODIFY_LABEL = 6;
	public static final int DELETE_LABEL = 7;
	
	public static Log createNode(int id, int type) throws HdglException{
		return new Log(CREATE_NODE, null, id, type);
	}
	
	public static Log createRelationShip(int id, int type, int node1, int node2) throws HdglException{
		return new Log(CREATE_NODE, null, id, type, node1, node2);
	}
	
	public static Log deleteNode(int id) throws HdglException{
		return new Log(DELETE_NODE, null, id);
	}
	
	public static Log deleteRelationship(int id) throws HdglException{
		return new Log(DELETE_RELATIONSHIP, null, id);
	}
	
	public static Log addLabel(int id, int labelName, Object value, Configuration conf) throws HdglException{
		Log log= new Log(ADD_LABEL, value, id, labelName);
		log.configuration = conf;
		return log;
	}
	
	public static Log deleteLabel(int id, int labelName) throws HdglException{
		return new Log(DELETE_LABEL, null, id, labelName);
	}
	
	public static Log modifyLabel(int id, int labelName, Object value, Configuration conf) throws HdglException{
		Log log=  new Log(MODIFY_LABEL, value, id, labelName);
		log.configuration = conf;
		return log;
	}
	
	int type;
	int[] ids;
	Object data;
	Configuration configuration;
	
	/**
	 * 创建一个准备用于读取的Log
	 */
	public Log(){
		configuration = new Configuration();
	}
	
	private Log(int type, Object data, int... ids) throws HdglException{
		if(data!=null&&!(data instanceof Writable)&&!(data instanceof Serializable)){
			throw new HdglException.DataIsNotWritableOrSerializableException();
		}
		this.type = type;
		this.ids = ids;
		this.data = data;	
	}
	
	private int getFlag(){
		int hasData;
		if(data!=null){
			hasData = 1;
		}else{
			hasData = 0;
		}
		return (type<<24) | ((ids.length & 0x0000ffff)<<8 ) | (hasData & 0x000000ff);
	}
	
	private boolean readFlag(int flag){
		boolean hasdata = (flag & 0x000000ff) > 0;
		int len = (flag&0x00ffff00)>>8;
		ids = new int[len];
		type = flag >>> 24;
		return hasdata;
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(getFlag());
		for(int i:ids){
			out.writeInt(i);
		}
		if(data!=null){
			ObjectWritable.writeObject(out, data, data.getClass(), configuration, true);
		}		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		boolean hasdata = readFlag(in.readInt());
		for(int i=0;i<ids.length;i++){
			ids[i]=in.readInt();
		}
		if(hasdata){
			data = ObjectWritable.readObject(in, configuration);
		}
	}

}
