package hdgl.db.impl.hadoop;

import static org.junit.Assert.*;

import hdgl.db.graph.Graph;
import hdgl.db.graph.MutableGraph;

import java.io.IOException;



import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class HGraphTest {

	@Test
	public void test() throws IOException, InterruptedException {
		Configuration conf = new Configuration();
		Graph graph = new HGraph(conf);
		MutableGraph mutableGraph = graph.beginModify();
	}

}
