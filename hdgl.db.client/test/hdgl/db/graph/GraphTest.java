package hdgl.db.graph;

import static org.junit.Assert.*;
import hdgl.db.conf.GraphConf;

import java.util.concurrent.ExecutionException;

import org.apache.hadoop.conf.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraphTest {
	
	@Test
	public void test() throws InterruptedException, ExecutionException {
		Configuration configuration = GraphConf.getDefault();
		Graph g = GraphFactory.connect(configuration);
		MutableGraph m = g.beginModify();
		Vertex v1 = m.createVertex("v");
		Vertex v2 = m.createVertex("v");
		Vertex v3 = m.createVertex("v2");
		Edge e1 = m.createEdge("e", v1, v2);
		Edge e2 = m.createEdge("e", v2, v3);
		Edge e3 = m.createEdge("e2", v3, v1);
		m.setLabel(v1, "title", "v1".getBytes());
		m.setLabel(v2, "title", "v2".getBytes());
		m.setLabel(v3, "title", "v3".getBytes());
		m.setLabel(e1, "title", "e1".getBytes());
		m.setLabel(e2, "title", "e2".getBytes());
		m.setLabel(e3, "title", "e3".getBytes());
		System.out.println(m.commit().get());
	}

}
