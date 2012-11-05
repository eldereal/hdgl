package hdgl.db.store;
import java.io.IOException;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;

public class PersistentGraph {
	public static class PersistentGraphMapper1 extends 
		Mapper<LongWritable, Text, Text, Text>
	{
		static final int POS_ADD = 4;
		static final int POS_LABEL = 10;
		static final int POS_EDGE = 9;
		static int posV = 0;
		static int posE = 0;
		static String[] str;
		static String value_str;
		public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException
		{
			value_str = value.toString().substring(1, value.toString().length()-1);
			posV = value_str.indexOf("vertex");
			posE = value_str.indexOf("edge");
			str = value_str.split(":");
			if (posV == POS_LABEL)
			{
				context.write(new Text(str[0].substring(POS_LABEL)), new Text("l " + str[1]));
			}
			else if (posV == POS_ADD)
			{
				context.write(new Text(str[0].substring(POS_ADD)), new Text("  "));
			}
			if (posE == POS_ADD)
			{
				String[] vertexInEdge = str[1].split(" - ");
				context.write(new Text("vertex " + vertexInEdge[0]), new Text("o " + str[0].substring(POS_EDGE) + " " + vertexInEdge[1]));
				context.write(new Text("vertex " + vertexInEdge[1]), new Text("i " + str[0].substring(POS_EDGE) + " " + vertexInEdge[0]));
				context.write(new Text(str[0].substring(POS_ADD)), new Text("  " + str[1]));				
			}
			else if (posE == POS_LABEL)
			{
				context.write(new Text(str[0].substring(POS_LABEL)), new Text("l " + str[1]));
			}
		}
	}
	
	public static class PersistentGraphPartitioner extends
	HashPartitioner<Text, Text>
	{
		public int getPartition(Text key, Text value, int numReduceTasks)
		{
			char type = key.toString().charAt(0);
			String str;
			if (type == 'v') str = key.toString().substring(7);
			else str = key.toString().substring(5);
			int id = Integer.parseInt(str);
			int contain_v = Parameter.VertexNumber / numReduceTasks + 1;
			int contain_e = Parameter.EdgeNumber / numReduceTasks + 1;
			if (type == 'v')
			{
				return (id / contain_v);
			}
			else
			{
				return (id / contain_e);
			}
		}
	}
	
	public static class PersistentGraphReducer1 extends
		Reducer<Text, Text, NullWritable, GraphWritable>
	{
		private MultipleOutputs<NullWritable, GraphWritable> mos;
		 
		public void setup(Context context)
		{
			mos = new MultipleOutputs<NullWritable, GraphWritable>(context);
		}
		
		public void cleanup(Context context) 
			throws IOException, InterruptedException 
		{
			mos.close();
		}
		
		private long offsetVertex = 0;
		private long offsetEdge = 0;
		
		public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException
		{
			char typeVE = key.toString().charAt(0);
			int id = Integer.parseInt(key.toString().substring(key.toString().indexOf(" ") + 1));
			char type;
			String value;
			
			if (typeVE == 'v')
			{
				Vertex vertex = new Vertex(id);
				for (Text val : values)
				{
					type = val.toString().charAt(0);
					value = val.toString().substring(2);
					switch (type)
					{
					case 'o':
						vertex.addEdgesAggregate(-1, value, " ");
						break;
					case 'i':
						vertex.addEdgesAggregate(1, value, " ");
						break;
					case 'l':
						vertex.addLabelAggregate(value, " = ");
						break;
					}
				}
				offsetVertex += vertex.prepareData(offsetVertex);
				if (vertex.getNeedIrr())
				{
					vertex.setIrr(true);
					mos.write(Parameter.VERTEX_IRREGULAR_FILE_NAME, NullWritable.get(), vertex);
				}
				vertex.setIrr(false);
				mos.write(Parameter.VERTEX_REGULAR_FILE_NAME, NullWritable.get(), vertex);
			}
			else if(typeVE == 'e')
			{
				Edge edge = new Edge(id);
				for (Text val : values)
				{
					type = val.toString().charAt(0);
					value = val.toString().substring(2);
					switch (type)
					{
					case ' ':
						edge.setTwoVertex(value, " - ");
						break;
					case 'l':
						edge.addLabelAggregate(value, " = ");
						break;
					}
				}
				offsetEdge += edge.prepareData(offsetEdge);
				if (edge.getNeedIrr())
				{
					edge.setIrr(true);
					mos.write(Parameter.EDGE_IRREGULAR_FILE_NAME, NullWritable.get(), edge);
				}
				edge.setIrr(false);
				mos.write(Parameter.EDGE_REGULAR_FILE_NAME, NullWritable.get(), edge);
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
		conf.set(Parameter.CONF_ARG1, Parameter.CONF_ARG2);
		Path inPath = new Path(Parameter.IN_PATH);
		Path outPath = new Path(Parameter.OUT_PATH);
		Job job1 = new Job(conf, "PersistentGraph");
		job1.setJarByClass(PersistentGraph.class);
		
		FileSystem hdfs1 = FileSystem.get(conf);
		hdfs1.delete(outPath, true);
		FileInputFormat.addInputPath(job1, inPath);
		FileOutputFormat.setOutputPath(job1, outPath);
		
		job1.setMapperClass(PersistentGraphMapper1.class);
		job1.setReducerClass(PersistentGraphReducer1.class);
		
		job1.setInputFormatClass(TextInputFormat.class);
		job1.setMapOutputKeyClass(Text.class);
		job1.setMapOutputValueClass(Text.class);
		job1.setPartitionerClass(PersistentGraphPartitioner.class);
		MultipleOutputs.addNamedOutput(job1, Parameter.VERTEX_IRREGULAR_FILE_NAME, GraphOutputFormat.class, NullWritable.class, GraphWritable.class);
		MultipleOutputs.addNamedOutput(job1, Parameter.EDGE_IRREGULAR_FILE_NAME, GraphOutputFormat.class, NullWritable.class, GraphWritable.class);
		MultipleOutputs.addNamedOutput(job1, Parameter.VERTEX_REGULAR_FILE_NAME, GraphOutputFormat.class, NullWritable.class, GraphWritable.class);
		MultipleOutputs.addNamedOutput(job1, Parameter.EDGE_REGULAR_FILE_NAME, GraphOutputFormat.class, NullWritable.class, GraphWritable.class);
		job1.waitForCompletion(true);
	}
}