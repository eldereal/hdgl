package hdgl.db.store;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import hdgl.db.conf.GraphConf;
import hdgl.db.store.impl.hdfs.mapreduce.MutableGraph;
import hdgl.util.ByteArrayHelper;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class CreateBigGraph {

	@Test
	public void test() throws Exception {
		Configuration conf = GraphConf.getDefault();
		MutableGraph m = new MutableGraph(conf, 1);
		int vertex=1000000;
		int edge=100;
		Map<Integer, Long> vids = new HashMap<Integer, Long>();
		for(int i=1;i<=vertex;i++){
			vids.put(i, m.createVertex("v"));
		}
		int ecount = 0;
		for(int i=1;i<=vertex;i++){
			for(int j=1;j<=vertex;j++){
				if(Math.random()<edge/(double)vertex){
					ecount++;
					m.createEdge("e", vids.get(i), vids.get(j));
				}
			}
		}
		System.out.println("create "+vertex+" vertives and "+ecount+" edges.");
		assertTrue(m.commit().get());

	}

}
