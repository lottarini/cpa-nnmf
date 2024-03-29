package ExternalPhase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import util.SparseElement;
import util.SparseVectorElement;

public class TextToSequenceSparseElementTranslator {

	public static class MyMapper extends Mapper<LongWritable, Text, IntWritable, SparseVectorElement> {

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			SparseElement se = SparseElement.parseLine(value.toString());
			SparseVectorElement sve = new SparseVectorElement(se.getColumn(), se.getValue());
			context.write(new IntWritable(se.getRow()), sve);
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("The number of the input parameter are not corrected");
			System.err.println("First Parameter: A/W files directories");
			System.err.println("Second Parameter: Output directory");
			System.exit(-1);
		}

		Configuration conf = new Configuration();

		Job job = new Job(conf, "Translator from Text to Sequence for the Sparse Element");
		job.setJarByClass(TextToSequenceSparseElementTranslator.class);
		job.setMapperClass(MyMapper.class);

		job.setNumReduceTasks(0);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(SparseVectorElement.class);

		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		TextInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}
}
