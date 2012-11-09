package hdgl.db.store;

import static org.junit.Assert.*;

import java.io.IOException;

import hdgl.db.conf.GraphConf;
import hdgl.db.store.impl.hdfs.HdfsGraphStore;
import hdgl.db.store.impl.hdfs.mapreduce.MutableGraph;
import hdgl.db.store.impl.hdfs.mapreduce.PersistentGraph;
import hdgl.util.ByteArrayHelper;

import org.apache.hadoop.conf.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

public class HdfsStoreTest {

	@Test 
	public void otherTest() throws IOException, ClassNotFoundException, InterruptedException {
//		Configuration conf = GraphConf.getDefault();
//
//		MutableGraph mg = new MutableGraph(conf, 0);
//		mg.createVertex("wuhanzhao");
//		mg.createVertex("chenshi");
//		mg.createVertex("无定所");
//		mg.setLabel(-1, "name", new String("why").getBytes());
//		mg.setLabel(-1, "国家", new String("中国").getBytes());
//		mg.setLabel(-2, "姓名", new String("武汉着").getBytes());
//		mg.setLabel(-3, "姓名", new String("afds").getBytes());
//		mg.createEdge("边", -1, -2);
//		mg.createEdge("苏打粉", -2, -3);
//		mg.setLabel(1, "关系", new String("不知道").getBytes());
//		mg.setLabel(2, "关系", new String("知道").getBytes());
//		mg.close();
//		PersistentGraph p = new PersistentGraph(conf, 0, mg.getVertexNum(), mg.getEdgeNum());
//		p.runMapReduce();
//		HdfsGraphStore hg = new HdfsGraphStore(conf);
//		hg.parseVertex(-1);
//		hg.parseEdge(1);
	}
	static GraphStore g;
	@BeforeClass
	public static void beforeClass() throws IOException{
		Configuration conf = GraphConf.getDefault();

		g = StoreFactory.createGraphStore(conf);
	}
	
	@Test
	public void UseExampleTest() throws IOException {
		for(int i=0;i<1000;i++){
			g.parseVertex(1);
		}
		assertEquals(5, g.getVertexCount());
		assertEquals(9, g.getEdgeCount());
		assertEquals("person", g.parseVertex(1).getType());
		assertEquals("back", g.parseEdge(-5).getType());
		assertEquals("forward", g.parseEdge(-1).getType());
		assertEquals("jump", g.parseEdge(-8).getType());
		assertEquals(-4, ByteArrayHelper.parseInt(g.parseEdge(-5).getLabel("len")));
	}
	
	

}
