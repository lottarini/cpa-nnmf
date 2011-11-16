package WPhaseSequence;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import util.GenericWritablePhase1;
import util.IntAndIdWritable;
import util.MatrixVector;
import util.SparseVectorElement;


/**
 *
 * @author virgilid
 */
public class WPhase1 {

	private static boolean H = false;
	//private static int currentRow;

	/* The output values must be text in order to distinguish the different data types */
	public static class MyMapper extends Mapper<IntWritable, GenericWritablePhase1, IntAndIdWritable, GenericWritablePhase1> {

		@Override
		protected void setup(Context context) throws IOException
		{

		    MatrixVector.setElementsNumber(context.getConfiguration().getInt("elementsNumber", 0));

			String folderName = ((FileSplit) context.getInputSplit()).getPath().getParent().getName();


			/*  the number present in the file name is the number of the first stored row vector
                            Through a static variable we take into account the right row number knowing that the
                            row vector are read sequentially in the file split
			*/

			if (folderName.startsWith("H")) /* A row vector must be emitted */
			{

				H = true;
			}
			else if( ! folderName.startsWith("A")) throw new IOException("File name not correct");
		}

		@Override
		public void map(IntWritable key, GenericWritablePhase1 value, Context context) throws IOException, InterruptedException
		{

			if (H)
			{
				context.write(new IntAndIdWritable(key.get(),'H'), value );
			}
			else  /* The sparse element must be emitted */
			{
				SparseVectorElement sve = (SparseVectorElement) value.get();
				int column = sve.getCoordinate();
				int row = key.get();
				GenericWritablePhase1 out = new GenericWritablePhase1();
				SparseVectorElement out1 = new SparseVectorElement(row,sve.getValue());
				out.set(out1);
				context.write(new IntAndIdWritable(column,'a'), out);
			}
		}
//lower case is usefull for the ordering of the key

	}


	public static class MyReducer extends Reducer<IntAndIdWritable, GenericWritablePhase1, IntWritable, MatrixVector> {
		protected void setup(Context context){
		    		    MatrixVector.setElementsNumber(context.getConfiguration().getInt("elementsNumber", 0));
		}
		@Override
		public void reduce(IntAndIdWritable key, Iterable<GenericWritablePhase1> values, Context context) throws IOException, InterruptedException
		{
                        System.out.println("REDUCE KEY:" +key);

			MatrixVector mv = null,temp = null;

			SparseVectorElement val = null;

			Iterator<GenericWritablePhase1> iter = values.iterator();

			if(iter.hasNext())
			{
				temp = (MatrixVector) iter.next().get();
				mv = new MatrixVector(temp.getNumberOfElement(), temp.getValues().clone());
				System.out.println("VETTORE: "+mv.toString());

			}
			while (iter.hasNext())
			{
				val = (SparseVectorElement) iter.next().get();
				System.out.println("SPARSE ELEMENT" + val.toString());
				if (val.getValue() != 0.0)
				{
					MatrixVector mvEmit =  mv.ScalarProduct(val.getValue());
					context.write(new IntWritable(val.getCoordinate()), mvEmit);
				}
			}
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		if(args.length != 4)
		{
			System.err.println("The number of the input parameter are not corrected");
			System.err.println("First/Second Parameter: A/W files directories");
			System.err.println("Third Parameter: Output directory");
			System.exit(-1);
		}

		Configuration conf = new Configuration();
		conf.setInt("elementsNumber", Integer.parseInt(args[3]));

		Job job = new Job(conf, "W Step1");
		job.setJarByClass(WPhase1.class);
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);

		job.setMapOutputKeyClass(IntAndIdWritable.class);
		job.setMapOutputValueClass(GenericWritablePhase1.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(MatrixVector.class);

		//job.setPartitionerClass(FirstPartitioner.class);
		job.setGroupingComparatorClass(IntWritable.Comparator.class);

		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		// Testing Job Options
		job.setNumReduceTasks(2);
		//job.setOutputValueGroupingComparator(Class);

		TextInputFormat.addInputPath(job, new Path(args[0]));
		TextInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		job.waitForCompletion(true);
	}
}
