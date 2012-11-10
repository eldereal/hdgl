package hdgl.db.store;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import hdgl.db.conf.GraphConf;
import hdgl.db.store.impl.hdfs.HdfsGraphStore;
import hdgl.db.store.impl.hdfs.mapreduce.FSDataInputStreamPool;
import hdgl.db.store.impl.hdfs.mapreduce.HEdge;
import hdgl.db.store.impl.hdfs.mapreduce.HVertex;
import hdgl.db.store.impl.hdfs.mapreduce.MutableGraph;
import hdgl.db.store.impl.hdfs.mapreduce.Parameter;
import hdgl.db.store.impl.hdfs.mapreduce.PersistentGraph;
import hdgl.util.ByteArrayHelper;
import hdgl.util.StringHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.BeforeClass;
import org.junit.Test;

public class HdfsStoreTest {

	@Test 
	public void otherTest() throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
		Configuration conf = GraphConf.getDefault();

		MutableGraph mg = new MutableGraph(conf, 2);
		for (int i = 1; i < 11; i++)
		{
			mg.createVertex("wuhanzhao");
			mg.createVertex("chenshi");
			mg.createVertex("无定所");
			mg.setLabel(-(i-1)*3-1, "name", new String("why").getBytes());
			mg.setLabel(-(i-1)*3-1, "国家", new String("中国").getBytes());
			for (int j = 0; j < 60; j++)
			{
				mg.setLabel(-(i-1)*3-1, "姓名" + j, new String("武afajf发送反对汉着").getBytes());
			}
			mg.setLabel(-(i-1)*3-2, "姓名", new String("afds").getBytes());
			mg.setLabel(-(i-1)*3-2, "姓名1", new String("afds").getBytes());
			mg.setLabel(-(i-1)*3-2, "姓名2", new String("afds").getBytes());
			mg.setLabel(-(i-1)*3-3, "姓名2", new String("afds").getBytes());
			mg.setLabel(-(i-1)*3-3, "姓名3", new String("afds").getBytes());
			mg.createEdge("边", -(i-1)*3-1, -(i-1)*3-2);
			mg.createEdge("苏打粉", -(i-1)*3-1, -(i-1)*3-3);
			mg.setLabel((i-1)*2+1, "关系", new String("不知道").getBytes());
			mg.setLabel((i-1)*2+2, "关系", new String("知道").getBytes());
			for (int j = 0; j < 60; j++)
			{
				mg.setLabel((i-1)*2+2, "关系" + j, new String("知啊大哥人格道").getBytes());
			}
		}
		mg.close();
		assertTrue(mg.commit().get());
		HdfsGraphStore hg = new HdfsGraphStore(conf);
		HVertex hv;
		hv = (HVertex) hg.parseVertex(1);
		assertEquals("wuhanzhao", hv.getType());
		assertEquals("武afajf发送反对汉着", new String(hv.getLabel("姓名56")));
		assertEquals("武afajf发送反对汉着", new String(hv.getLabel("姓名15")));
		hv = (HVertex) hg.parseVertex(5);
		assertEquals("afds", new String(hv.getLabel("姓名1")));
		assertEquals("chenshi", hv.getType());
		hv = (HVertex) hg.parseVertex(10);
		assertEquals(10, hv.getId());
		assertEquals("wuhanzhao", hv.getType());
		assertEquals("武afajf发送反对汉着", new String(hv.getLabel("姓名56")));
		assertEquals("武afajf发送反对汉着", new String(hv.getLabel("姓名15")));
		HEdge he = (HEdge) hg.parseEdge(-1);
		assertEquals("边", he.getType());
		assertEquals("不知道", new String(he.getLabel("关系")));
		he = (HEdge) hg.parseEdge(-4);
		assertEquals(-4, he.getId());
		assertEquals("苏打粉", he.getType());
		assertEquals("知啊大哥人格道", new String(he.getLabel("关系10")));
		assertEquals("知啊大哥人格道", new String(he.getLabel("关系50")));
	}
	static GraphStore g;
	static FileSystem hdfs;
	static FSDataInputStream fs, fs1;
	static String str;
	static int size;
	@BeforeClass
	public static void beforeClass() throws IOException{
		Configuration conf = GraphConf.getDefault();

		g = StoreFactory.createGraphStore(conf);
		hdfs = FileSystem.get(conf);
		str = GraphConf.getPersistentGraphRoot(conf);
		size = GraphConf.getVertexTrunkSize(conf);
	}
	
	@Test
	public void UseExampleTest() throws IOException {
		/*for(int i=0;i<1000;i++){
			g.parseVertex(1);
		}
		assertEquals(5, g.getVertexCount());
		assertEquals(9, g.getEdgeCount());
		assertEquals("person", g.parseVertex(1).getType());
		assertEquals("back", g.parseEdge(-5).getType());
		assertEquals("forward", g.parseEdge(-1).getType());
		assertEquals("jump", g.parseEdge(-8).getType());
		assertEquals(-4, ByteArrayHelper.parseInt(g.parseEdge(-5).getLabel("len")));*/
		/*for (int i = 1; i <= 100000; i++)
		{
			HVertex hv;
			hv = (HVertex) g.parseVertex(i);
			System.out.println(i + " vs " + hv.getId());
			//assertEquals(i, hv.getId());
			System.out.println("type : " + hv.getType());
			//assertEquals("v", hv.getType());
			if (i % 1000 == 0) System.out.println(".");
		}
		FSDataInputStreamPool.close();*/
		/*Path path = new Path(str + "/" + Parameter.VERTEX_REGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(0));
		fs = hdfs.open(path);
		
		long len = hdfs.getFileStatus(path).getLen() / size;
		System.out.println(len);
		fs = hdfs.open(path);
		fs1 = hdfs.open(new Path(str + "/" + Parameter.EDGE_REGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(0)));
		byte[] b = new byte[10];
		for (int i = 0; i < len; i++)
		{
			
			fs.seek(i * size);
			fs1.seek(i*size);
			for (int j = 0; j < 20; j++)
			{
				fs.readInt();
				fs1.readInt();
			}
			fs.read(b);
			fs1.read(b);
			//fs.close();
			if (i % 1000 == 0) 
			{
				fs.close();
				System.out.println(".");
			}
		}
		fs.close();*/
		
	}	

}
