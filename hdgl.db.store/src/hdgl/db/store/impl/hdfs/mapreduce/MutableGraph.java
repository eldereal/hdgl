package hdgl.db.store.impl.hdfs.mapreduce;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;



import hdgl.db.graph.Entity;
import hdgl.db.graph.HGraphIds;
import hdgl.db.task.AsyncResult;

import java.io.IOException;

public class MutableGraph implements hdgl.db.graph.MutableGraph {
	
	private FSDataOutputStream outputStream;
	private FileSystem hdfs;
	private long vertex = 0;
	private long edge = 0;
	
	public MutableGraph(String name)
	{
		try
		{
			Configuration conf = new Configuration();
			conf.set(Parameter.CONF_ARG1, Parameter.CONF_ARG2);
			hdfs = FileSystem.get(conf);
			Path dfs = new Path(name);
			outputStream = hdfs.create(dfs, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void close() 
	{
		try 
		{
			outputStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public long createVertex()
	{
		vertex++;
		StringBuffer line = new StringBuffer("[add vertex ");
		line.append(vertex);
		line.append(":]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return vertex;
	}
	public long createEdge(long vertex1, long vertex2)
	{
		edge--;
		StringBuffer line = new StringBuffer("[add edge ");
		line.append(edge);
		line.append(":");
		line.append(vertex1 + " - " + vertex2);
		line.append("]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return edge;
	}
	public void setVertexLabel(long vertex, String name, String value)
	{
		StringBuffer line = new StringBuffer("[add label vertex ");
		line.append(vertex);
		line.append(":" + name + " = ");
		line.append(value);
		line.append("]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void setEdgeLabel(long edge, String name, String value)
	{
		StringBuffer line = new StringBuffer("[add label edge ");
		line.append(edge);
		line.append(":" + name + " = ");
		line.append(value);
		line.append("]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception
	{
		MutableGraph mg = new MutableGraph("logdata");
		mg.addVertex();
		mg.addVertex();
		mg.addVertex();
		mg.addVertex();
		mg.addVertex();
		mg.addEdge(0, 1);
		mg.addEdge(1, 2);
		mg.addEdge(2, 3);
		mg.addEdge(2, 4);
		mg.addEdge(3, 4);
		mg.addEdge(0, 2);
		mg.addEdge(0, 3);
		mg.addEdge(0, 4);
		mg.setVertexLabel(0, "name", "www");
		mg.setVertexLabel(0, "city", "Beijing");
		mg.setVertexLabel(0, "age", "17");
		mg.setVertexLabel(1, "name", "ok");
		mg.setVertexLabel(1, "sex", "male");
		mg.setVertexLabel(2, "name", "good");
		mg.setVertexLabel(3, "name", "wuhanzhao");
		mg.setVertexLabel(3, "city", "Beijing");
		mg.setVertexLabel(3, "age", "22");
		mg.setVertexLabel(3, "birthday", "unknow");
		mg.setVertexLabel(3, "学校", "清华大学");
		mg.setEdgeLabel(0, "relation", "friend");
		mg.setEdgeLabel(0, "afd", "adfsa");
		mg.addEdge(1, 2);
		mg.setEdgeLabel(1, "relation", "lover");
		mg.setEdgeLabel(2, "relation", "abcdefghijklmnabcdefghijklmn");
		mg.setEdgeLabel(3, "relation", "mm");
		mg.setEdgeLabel(4, "relation", "cc");
		mg.setEdgeLabel(5, "relation", "lover");
		mg.setEdgeLabel(5, "belong", "Yes");
		mg.setEdgeLabel(5, "试试", "可不可以");
		mg.setEdgeLabel(6, "belong", "Yes");
		mg.close();
	}
	
	@Override
	public AsyncResult<Boolean> commit() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AsyncResult<Boolean> abort() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long createVertex(String type) {
		long id = createVertex();
		setVertexLabel(id, "type", type);
		return id;
	}
	@Override
	public long createEdge(String type, long start, long end) {
		long id=createEdge(start, end);
		setEdgeLabel(id, "type", type);
		return id;
	}
	@Override
	public void setLabel(long entity, String name, byte[] value) {
		if(HGraphIds.isEdgeId(entity)){
			setEdgeLabel(entity, name, value);
		}else{
			setVertexLabel(entity, name, value);
		}		
	}
	@Override
	public void deleteEntity(Entity e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteLabel(Entity e, String name) {
		// TODO Auto-generated method stub
		
	}
}
