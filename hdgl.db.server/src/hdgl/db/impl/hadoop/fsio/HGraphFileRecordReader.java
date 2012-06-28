package hdgl.db.impl.hadoop.fsio;

import hdgl.db.impl.hadoop.HConf;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileRecordReader;

public class HGraphFileRecordReader extends
		RecordReader<IntWritable, HGraphFileRecord> {

	private static final int HEADER_SIZE=0;
	
	SequenceFileRecordReader<NullWritable, HGraphFileRecord> wrapped;
	
	@Override
	public synchronized void close() throws IOException {
		in.close();
	}

	@Override
	public IntWritable getCurrentKey() throws IOException, InterruptedException {
		return key;
	}

	@Override
	public HGraphFileRecord getCurrentValue() throws IOException,
			InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		if (end == start) {
			return 0.0f;
		} else {
			return Math.min(1.0f, (in.getPosition() - start)
					/ (float) (end - start));
		}
	}

	@Override
	public void initialize(InputSplit input, TaskAttemptContext task)
			throws IOException, InterruptedException {
		FileSplit fileSplit = (FileSplit) input;
		Path path = fileSplit.getPath();
		FileSystem fs = path.getFileSystem(conf);
		recordSize = HConf.getARecordSize(conf);
		this.in = new SequenceFile.Reader(fs, path, conf);
		this.end = fileSplit.getStart() + fileSplit.getLength();

		if (fileSplit.getStart() > in.getPosition()) {
			in.seek(fileSplit.getStart()); // sync to start
		}

		this.start = in.getPosition();
		more = start < end;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (!more) {
			return false;
		}
		long pos = in.getPosition();
		if (!in.next(nokey, value) || (pos >= end)) {
			more = false;
			key = null;
			value = null;
		}
		return more;
	}
}
