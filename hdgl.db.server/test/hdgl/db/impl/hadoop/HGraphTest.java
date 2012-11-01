package hdgl.db.impl.hadoop;

import static org.junit.Assert.*;

import hdgl.db.graph.deprecated.Graph;
import hdgl.db.graph.deprecated.LabelContainer;
import hdgl.db.graph.deprecated.MutableGraph;
import hdgl.db.graph.deprecated.Node;
import hdgl.db.graph.deprecated.Vertex;

import java.io.IOException;
import java.util.concurrent.ExecutionException;



import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class HGraphTest {

	static class TestNode extends Node{

		public TestNode(int id, LabelContainer labelImpl, Vertex vertexImpl) {
			super(id, labelImpl, vertexImpl);			
		}
		
	}
	
	static class TestNode2 extends Node{

		public TestNode2(int id, LabelContainer labelImpl, Vertex vertexImpl) {
			super(id, labelImpl, vertexImpl);			
		}
		
	}
	
	static class TestTestNode extends TestNode{

		public TestTestNode(int id, LabelContainer labelImpl, Vertex vertexImpl) {
			super(id, labelImpl, vertexImpl);			
		}
		
	}
	
	@Test
	public void test() throws Exception{
		Configuration conf = new Configuration();
		conf.set("hdgl.graph.namespace", "test");
		conf.set("fs.defaultFS", "hdfs://localhost:9000");
		Graph graph = null;
		try{
			graph = new HGraph(conf);
			MutableGraph mutableGraph = graph.beginModify();
			TestNode node = mutableGraph.createNode(TestNode.class);
			TestNode2 node2=mutableGraph.createNode(TestNode2.class);
			TestTestNode testNode=mutableGraph.createNode(TestTestNode.class);
			if(!mutableGraph.commit().get()){
				fail("commit failed");
			}
		}finally{
			if(graph!=null){
				graph.close();
			}
		}
		
		
	}

}
