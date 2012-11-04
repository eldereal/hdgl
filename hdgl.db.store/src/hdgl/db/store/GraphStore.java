package hdgl.db.store;

import java.io.IOException;
import java.io.InputStream;

public class GraphStore {

	public InputStream getVertexData(long id) throws IOException
	{
		VertexInputStream vis = new VertexInputStream(id);
		return vis;
	}
	
	public InputStream getEdgeData(long id) throws IOException
	{
		EdgeInputStream eis = new EdgeInputStream(id);
		return eis;
	}
	
	public Vertex parseVertex(long id) throws IOException
	{
		VertexInputStream vis = (VertexInputStream) getVertexData(id);
		Vertex v = new Vertex(vis.readInt());
		int outNum, edge, vertex, inNum, num;
		outNum = vis.readInt();
		inNum = vis.readInt();
		for (int i = 0; i < outNum; i++)
		{
			edge = vis.readInt();
			vertex = vis.readInt();
			v.addEdges(-1, edge, vertex);
		}
		for (int i = 0; i < inNum; i++)
		{
			edge = vis.readInt();
			vertex = vis.readInt();
			v.addEdges(1, edge, vertex);
		}
		num = vis.readInt();
		int len;
		for (int i = 0; i < num; i++)
		{
			len = vis.readInt();
			byte[] b = new byte[len];
			String key = null, value = null;
			if (len == vis.read(b))
			{
				key = new String(b);
			}
			len = vis.readInt();
			b = new byte[len];
			if (len == vis.read(b))
			{
				value = new String(b);
			}
			if (!((key.length() == 0) && (value.length() == 0)))
			{
				v.addLabel(key, value);
			}
		}
		return v;
	}
	
	public Edge parseEdge(long id) throws IOException
	{
		EdgeInputStream eis = (EdgeInputStream) getEdgeData(id);
		Edge e = new Edge(eis.readInt());
		e.setVertex1(eis.readInt());
		e.setVertex2(eis.readInt());
		int num;
		num = eis.readInt();
		int len;
		for (int i = 0; i < num; i++)
		{
			len = eis.readInt();
			byte[] b = new byte[len];
			String key = null, value = null;
			if (len == eis.read(b))
			{
				key = new String(b);
			}
			len = eis.readInt();
			b = new byte[len];
			if (len == eis.read(b))
			{
				value = new String(b);
			}
			e.addLabel(key, value);
		}
		return e;
	}
	
	public String[] bestPlacesForVertex(long entityId) throws IOException
	{
		return null;
	}
	
	public String[] bestPlacesForEdge(long entityId) throws IOException
	{
		return null;
	}
}
