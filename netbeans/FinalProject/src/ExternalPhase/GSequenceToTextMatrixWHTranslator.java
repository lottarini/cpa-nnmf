/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ExternalPhase;

import util.NMFVector;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import util.GenericElement;

public class GSequenceToTextMatrixWHTranslator {

	public static class MyMapper extends Mapper<IntWritable, GenericElement, IntWritable, NMFVector> {

		@Override
		protected void setup(Context context) {
			NMFVector.setElementsNumber(context.getConfiguration().getInt("elementsNumber", 0));
		}

		@Override
		public void map(IntWritable key, GenericElement values, Context context) throws IOException, InterruptedException {
			context.write(key, (NMFVector) values.get());
		}
	}

	public static class MyReducer extends Reducer<IntWritable, NMFVector, IntWritable, NMFVector> {

		@Override
		protected void setup(Context context) {
			NMFVector.setElementsNumber(context.getConfiguration().getInt("elementsNumber", 0));
		}

		@Override
		public void reduce(IntWritable key, Iterable<NMFVector> values, Context context) throws IOException, InterruptedException {

			Iterator<NMFVector> iter = values.iterator();
			while (iter.hasNext()) {
				context.write(key, iter.next());
			}

		}
	}


	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("The number of the input parameter are not corrected");
			System.err.println("First Parameter: HPhase1 output files directories");
			System.err.println("Second Parameter: Output directory");
			System.err.println("Third Parameter: The factorizing parameter of the NNMF (K)");
			System.exit(-1);
		}

		Configuration conf = new Configuration();
		conf.setInt("elementsNumber", Integer.parseInt(args[2]));

		Job job = new Job(conf, "Translator from Sequence to Text for the H/W Matrix");
		job.setJarByClass(GSequenceToTextMatrixWHTranslator.class);
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);

		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(NMFVector.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(NMFVector.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setNumReduceTasks(1);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}
}
