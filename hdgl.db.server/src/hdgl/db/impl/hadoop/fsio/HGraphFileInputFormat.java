package hdgl.db.impl.hadoop.fsio;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileRecordReader;

public class HGraphFileInputFormat extends FileInputFormat<IntWritable, HGraphFileRecord> {

	@Override
	public RecordReader<IntWritable, HGraphFileRecord> createRecordReader(
			InputSplit input, TaskAttemptContext task) throws IOException,
			InterruptedException {
		return new SequenceFileRecordReader<IntWritable, HGraphFileRecord>();
	}

}
