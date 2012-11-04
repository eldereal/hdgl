package hdgl.db.store;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.WritableComparable;


public class GraphWritable implements WritableComparable<GraphWritable>{
	protected int id;
	protected ArrayList<Label> labels;
	protected ByteBuffer bb = ByteBuffer.allocate(1048576);
	protected int count = 0;
	protected boolean needIrr = false;
	protected boolean isIrr = false;
	protected long offset = -1;
	
	public GraphWritable(int id)
	{
		this.id = id;
		labels = new ArrayList<Label>();
	}
	
	public boolean getNeedIrr()
	{
		return needIrr;
	}
	
	public long prepareData(long offset)
	{
		long ret = 0;
		
		this.offset = offset;
		if (count > Parameter.REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN) 
		{
			ret = count - Parameter.REGULAR_BLOCK_SIZE + Parameter.OFFSET_MAX_LEN;
			needIrr = true;
		}
		ret = ret + 4;
		return ret;
	}
	
	public void setIrr(boolean flag)
	{
		isIrr = flag;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException 
	{
	}

	@Override
	public void write(DataOutput output) throws IOException 
	{
		if (isIrr)
		{
			byte[] dst = new byte[count - Parameter.REGULAR_BLOCK_SIZE + Parameter.OFFSET_MAX_LEN];
			bb.position(Parameter.REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN);
			bb.get(dst, 0, count - Parameter.REGULAR_BLOCK_SIZE + Parameter.OFFSET_MAX_LEN);
			output.writeInt(dst.length);
			output.write(dst);
		}
		else
		{
			int temp;
			byte[] dst;
			if (Parameter.REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN > count) temp = count + Parameter.OFFSET_MAX_LEN;
			else temp = Parameter.REGULAR_BLOCK_SIZE;
			dst = new byte[Parameter.REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN];
			bb.position(0);
			bb.get(dst, 0, temp - Parameter.OFFSET_MAX_LEN);
			output.write(dst);
			output.writeLong(offset);
		}
	}

	@Override
	public int compareTo(GraphWritable o) 
	{
		return id - o.id;
	}
}
