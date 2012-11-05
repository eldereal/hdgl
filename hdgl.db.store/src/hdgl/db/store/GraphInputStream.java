package hdgl.db.store;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class GraphInputStream extends InputStream{
	private FSDataInputStream inputStream = null;
	private FileSystem hdfs;
	private long id;
	private long position = 0;
	private long limit = Parameter.REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN;
	protected String fileIrr = null;
	private long offset = -1;
	
	public GraphInputStream(long id) throws IOException
	{
		this.id = id;
		Configuration conf = new Configuration();
		conf.set(Parameter.CONF_ARG1, Parameter.CONF_ARG2);
		hdfs = FileSystem.get(conf);
	}
	
	public int locate(String file) throws IOException
	{
		long count = 0;
		int ret = 0;
		Path path = null;
		for (int i = 0; i < Parameter.REDUCER_NUMBER; i++)
		{
			path = new Path(file + "-r-" + Common.fillToLength(i));
			long temp = hdfs.getFileStatus(path).getLen() / Parameter.REGULAR_BLOCK_SIZE;
			if (count + temp > id)
			{
				ret = i;
				break;
			}
			count = count + temp;
		}
		inputStream = hdfs.open(path);
		inputStream.seek((id - count + 1)*Parameter.REGULAR_BLOCK_SIZE - 8);
		offset = inputStream.readLong();
		inputStream.seek((id - count)*Parameter.REGULAR_BLOCK_SIZE);
		return ret;
	}
	
	public void close() throws IOException
	{
		inputStream.close();
	}

	@Override
	public int read() throws IOException 
	{
		int ret;
		if (position >= limit)
		{
			if (offset == -1)
			{
				return -1;
			}
			changeFile();
			ret = inputStream.read();
		}
		else
		{
			ret = inputStream.read();
		}
		position++;
		return ret;
	}
	
	private void changeFile() throws IOException
	{
		Path path = new Path(fileIrr);
		inputStream.close();
		inputStream = hdfs.open(path);
		inputStream.seek(offset);
		position = 0;
		offset = -1;
		limit = inputStream.readInt();
	}
	
	public long skip(long n) throws IOException
	{
		if (limit - position >= n)
		{
			position = position + n;
			inputStream.skip(n);
			return n;
		}
		else
		{
			if (offset == -1)
			{
				inputStream.skip(limit - position);
				position = limit;
				return limit - position;
			}
			else
			{
				long skipNum = limit - position;
				return (skipNum + skip(n - limit + position));
			}
		}
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		int tmp;
		int count = 0;
		if (len == 0) return 0;
		if (off >= b.length) return 0;
		while (true)
		{
			tmp = read();
			if (tmp == -1)
			{
				return count;
			}
			b[off + count] = (byte) tmp;
			count++;
			if ((count >= len) || (off + count >= b.length))
			{
				return count;
			}
		}
	}
	
	public int read(byte[] b) throws IOException
	{
		int point = 0;
		int tmp;
		if (b.length == 0) return 0;
		while (true)
		{
			tmp = read();
			if (tmp == -1)
			{
				return point;
			}
			b[point] = (byte) tmp;
			point++;
			if (point >= b.length)
			{
				return point;
			}
		}
	}

	public int readInt() throws IOException
	{
		int[] bs = new int[4];
		int ret = 0;
		for (int i = 0; i < 4; i++)
		{
			bs[i] = read();
			if (bs[i] == -1)
			{
				bs[i] = 0;
			}
		}
		if (bs[0] >= 128)
		{
			
		}
		else
		{
			ret = (bs[0] << 24) + (bs[1] << 16) + (bs[2] << 8) + bs[3];
		}
		return ret;
	}
}