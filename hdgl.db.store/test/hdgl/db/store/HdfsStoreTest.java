package hdgl.db.store;

import static org.junit.Assert.*;

import java.io.IOException;

import hdgl.db.conf.GraphConf;
import hdgl.db.store.impl.hdfs.mapreduce.MutableGraph;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class HdfsStoreTest {

	@Test 
	public void writeTest() throws IOException {
		
		
	}
	
	@Test
	public void readTest() throws IOException {
		Configuration conf = GraphConf.getDefault();
		
		GraphStore gs = StoreFactory.createGraphStore(conf);
		
		for (int i = 0; i < 5; i++)
		{
			System.out.println(gs.parseVertex(i).getString());
		}
		for (int i = 0; i < 9; i++)
		{
			System.out.println(gs.parseEdge(i).getString());
		}
	}

}
