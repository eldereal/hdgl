package hdgl.db.store;

import java.io.IOException;

import hdgl.db.store.impl.cache.MemoryEdgeImpl;
import hdgl.db.store.impl.cache.MemoryGraphStore;
import hdgl.db.store.impl.cache.MemoryVertexImpl;
import hdgl.db.store.impl.hdfs.HdfsGraphStore;
import hdgl.db.store.impl.hdfs.HdfsLogStore;
import hdgl.util.IterableHelper;

import org.apache.hadoop.conf.Configuration;

public class StoreFactory {
	
	static byte[] data(Object obj){
		byte[] data=new byte[4];
		int code=obj.hashCode();
		data[0]=(byte) (code>>>24&0xff);
		data[1]=(byte) (code>>>16&0xff);
		data[2]=(byte) (code>>>8&0xff);
		data[3]=(byte) (code&0xff);
		return data;
	}
	
	public static GraphStore createGraphStore(Configuration conf) throws IOException{
		
		MemoryGraphStore g = new MemoryGraphStore();
		MemoryVertexImpl v1 = new MemoryVertexImpl(1l, "t1", 
				IterableHelper.<String, byte[]>makeMap("name", data("one"), "price", data(100)), 
				IterableHelper.<Long>makeSet(-5l), 
				IterableHelper.<Long>makeSet(-1l,-6l,-7l), g);
		MemoryVertexImpl v2 = new MemoryVertexImpl(2l, "t1", 
				IterableHelper.<String, byte[]>makeMap("name", data("two"), "price", data(50)), 
				IterableHelper.<Long>makeSet(-1l, -9l), 
				IterableHelper.<Long>makeSet(-2l, -8l), g);
		MemoryVertexImpl v3 = new MemoryVertexImpl(3l, "t1", 
				IterableHelper.<String, byte[]>makeMap("name", data("three"), "price", data(500)), 
				IterableHelper.<Long>makeSet(-2l, -6l), 
				IterableHelper.<Long>makeSet(-3l, -9l), g);
		MemoryVertexImpl v4 = new MemoryVertexImpl(4l, "t2", 
				IterableHelper.<String, byte[]>makeMap("name", data("four"), "price", data(10)), 
				IterableHelper.<Long>makeSet(-3l, -7l), 
				IterableHelper.<Long>makeSet(-4l), g);
		MemoryVertexImpl v5 = new MemoryVertexImpl(5l, "t2", 
				IterableHelper.<String, byte[]>makeMap("name", data("five"), "price", data(30)), 
				IterableHelper.<Long>makeSet(-4l, -8l), 
				IterableHelper.<Long>makeSet(-5l), g);
		MemoryEdgeImpl e1=new MemoryEdgeImpl(-1l, "forward", 1l, 2l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e2=new MemoryEdgeImpl(-2l, "forward", 2l, 3l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e3=new MemoryEdgeImpl(-3l, "forward", 3l, 4l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e4=new MemoryEdgeImpl(-4l, "forward", 4l, 5l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e5=new MemoryEdgeImpl(-5l, "back", 5l, 1l, 
				IterableHelper.<String, byte[]>makeMap("len", data(-4)), g);
		MemoryEdgeImpl e6=new MemoryEdgeImpl(-6l, "jump", 1l, 3l, 
				IterableHelper.<String, byte[]>makeMap("len", data(2)), g);
		MemoryEdgeImpl e7=new MemoryEdgeImpl(-7l, "jump", 1l, 4l, 
				IterableHelper.<String, byte[]>makeMap("len", data(3)), g);
		MemoryEdgeImpl e8=new MemoryEdgeImpl(-8l, "jump", 2l, 5l, 
				IterableHelper.<String, byte[]>makeMap("len", data(3)), g);
		MemoryEdgeImpl e9=new MemoryEdgeImpl(-9l, "back", 3l, 2l, 
				IterableHelper.<String, byte[]>makeMap("len", data(-1)), g);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addEdge(e1);
		g.addEdge(e2);
		g.addEdge(e3);
		g.addEdge(e4);
		g.addEdge(e5);
		g.addEdge(e6);
		g.addEdge(e7);
		g.addEdge(e8);
		g.addEdge(e9);
		return g;
	}
	
	public static LogStore createLogStore(Configuration conf, int sessionId) throws IOException{
		return new HdfsLogStore(conf, sessionId);
	}
}
