package hdgl.db.impl.hadoop.fsio;

import hdgl.db.impl.hadoop.HConf;

import java.io.IOException;

import javax.tools.JavaCompiler;

import org.apache.commons.logging.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.SerializationFactory;
import org.apache.hadoop.util.Options;
import org.apache.log4j.Logger;



public class HGraphFile {

	private static final Log LOG = LogFactory.getLog(HGraphFile.class);

	public static class Reader implements java.io.Closeable {

		private FSDataInputStream in;
		Configuration conf;
		private String filename;
		private long end;
	    private int recordSize;
	    
		/**
		 * A tag interface for all of the Reader options
		 */
		public static interface Option {
		}

		/**
		 * Create an option to specify the path name of the sequence file.
		 * 
		 * @param value
		 *            the path to read
		 * @return a new option
		 */
		public static Option file(Path value) {
			return new FileOption(value);
		}

		/**
		 * Create an option to specify the stream with the sequence file.
		 * 
		 * @param value
		 *            the stream to read.
		 * @return a new option
		 */
		public static Option stream(FSDataInputStream value) {
			return new InputStreamOption(value);
		}

		/**
		 * Create an option to specify the starting byte to read.
		 * 
		 * @param value
		 *            the number of bytes to skip over
		 * @return a new option
		 */
		public static Option start(long value) {
			return new StartOption(value);
		}

		/**
		 * Create an option to specify the number of bytes to read.
		 * 
		 * @param value
		 *            the number of bytes to read
		 * @return a new option
		 */
		public static Option length(long value) {
			return new LengthOption(value);
		}

		/**
		 * Create an option with the buffer size for reading the given pathname.
		 * 
		 * @param value
		 *            the number of bytes to buffer
		 * @return a new option
		 */
		public static Option bufferSize(int value) {
			return new BufferSizeOption(value);
		}

		private static class FileOption extends Options.PathOption implements
				Option {
			private FileOption(Path value) {
				super(value);
			}
		}

		private static class InputStreamOption extends
				Options.FSDataInputStreamOption implements Option {
			private InputStreamOption(FSDataInputStream value) {
				super(value);
			}
		}

		private static class StartOption extends Options.LongOption implements
				Option {
			private StartOption(long value) {
				super(value);
			}
		}

		private static class LengthOption extends Options.LongOption implements
				Option {
			private LengthOption(long value) {
				super(value);
			}
		}

		private static class BufferSizeOption extends Options.IntegerOption
				implements Option {
			private BufferSizeOption(int value) {
				super(value);
			}
		}

		// only used directly
		private static class OnlyHeaderOption extends Options.BooleanOption
				implements Option {
			private OnlyHeaderOption() {
				super(true);
			}
		}

		public Reader(FileSystem fs, Path file, Configuration conf)
				throws IOException {
			this(conf, file(file.makeQualified(fs)));
		}

		public Reader(Configuration conf, Option... opts) throws IOException {
			// Look up the options, these are null if not set
			FileOption fileOpt = Options.getOption(FileOption.class, opts);
			InputStreamOption streamOpt = Options.getOption(
					InputStreamOption.class, opts);
			StartOption startOpt = Options.getOption(StartOption.class, opts);
			LengthOption lenOpt = Options.getOption(LengthOption.class, opts);
			BufferSizeOption bufOpt = Options.getOption(BufferSizeOption.class,
					opts);

			// check for consistency
			if ((fileOpt == null) == (streamOpt == null)) {
				throw new IllegalArgumentException(
						"File or stream option must be specified");
			}
			if (fileOpt == null && bufOpt != null) {
				throw new IllegalArgumentException(
						"buffer size can only be set when"
								+ " a file is specified.");
			}
			// figure out the real values
			Path filename = null;
			FSDataInputStream file;
			final long len;
			if (fileOpt != null) {
				filename = fileOpt.getValue();
				FileSystem fs = filename.getFileSystem(conf);
				int bufSize = bufOpt == null ? getBufferSize(conf) : bufOpt
						.getValue();
				len = null == lenOpt ? fs.getFileStatus(filename).getLen()
						: lenOpt.getValue();
				file = openFile(fs, filename, bufSize, len);
			} else {
				len = null == lenOpt ? Long.MAX_VALUE : lenOpt.getValue();
				file = streamOpt.getValue();
			}
			long start = startOpt == null ? 0 : startOpt.getValue();
			recordSize = HConf.getARecordSize(conf);
			// really set up
			initialize(filename, file, start, len, conf);
		}

		private void initialize(Path filename, FSDataInputStream in,
				long start, long length, Configuration conf)
				throws IOException {
			if (in == null) {
				throw new IllegalArgumentException("in == null");
			}
			this.filename = filename == null ? "<unknown>" : filename
					.toString();
			this.in = in;
			this.conf = conf;
			boolean succeeded = false;
			try {
				seek(start);
				this.end = this.in.getPos() + length;
				// if it wrapped around, use the max
				if (end < length) {
					end = Long.MAX_VALUE;
				}
				succeeded = true;
			} finally {
				if (!succeeded) {
					IOUtils.cleanup(LOG, this.in);
				}
			}
		}

		 public synchronized void seek(long position) throws IOException {
		      in.seek(position);
		      
		 }
		
		private FSDataInputStream openFile(FileSystem fs, Path file,
				int bufferSize, long len) throws IOException  {
			return fs.open(file, bufferSize);
		}

		private int getBufferSize(Configuration conf2) {
			return conf.getInt("io.file.buffer.size", 4096);
		}

		@Override
		public void close() throws IOException {
			in.close();
		}

		public synchronized long getPosition() throws IOException {
			return in.getPos();
		}

		public synchronized boolean next(IntWritable key, HGraphFileRecord val)
			      throws IOException {
			long l = getPosition();
			if(l >= end){
				return false;
			}
			int k = (int) (l/recordSize);
			key.set(k);
			val.readFields(in);
			return true;
		}
		
	}

	public static class Writer implements java.io.Closeable{

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
