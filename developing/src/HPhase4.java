
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *
 * @author virgilid
 */
public class HPhase4 {

    /* The output values must be text in order to distinguish the different data types */
    public static class MyMapper extends Mapper<LongWritable, Text, IntWritable, MatrixVector> {

        private static MatrixMatrix WW;
        private static int currentColumn;

        protected void setup(Context context) throws IOException {
            // MI PRENDO LA MATRICE DAL FILE ESTERNO
            Configuration conf = context.getConfiguration();
            String otherFiles = conf.get("otherFiles", null);
            if (otherFiles != null) {
                FileSystem fs = FileSystem.get(conf);
                //creo il path dei file esterni
                Path inFile = new Path(otherFiles);
                FSDataInputStream in = fs.open(inFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String input;
                StringBuilder sb = new StringBuilder();
                input = br.readLine();
                while (!input.isEmpty()) {
                    sb.append(input);
                    input = br.readLine();
                }
                // stampa di debug del file esterno, seccata perche non so come stampa uno string builder

                WW = MatrixMatrix.parseLine(sb.toString()); //WW.parseLine(sb.toString());
            }

            // GUARDO DOVE SONO NELLA MATRICE H
            String chunkName = ((FileSplit) context.getInputSplit()).getPath().getName();
            int i, j;



            for (i = 0; i < chunkName.length()
                    && (chunkName.charAt(i) < '0' || chunkName.charAt(i) > '9'); i++);

            for (j = i; j < chunkName.length()
                    && (chunkName.charAt(j) >= '0' && chunkName.charAt(j) <= '9'); j++);

            try {
                String columnNumber = chunkName.substring(i, j);
                System.out.println("LA PRIMA COLONNA E:" + columnNumber);
                currentColumn = new Integer(columnNumber);
            } catch (NumberFormatException e) {
                throw new IOException("File name conversion failled");
            }
        }

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            MatrixVector mv = new MatrixVector(value);
            Context.write(new IntWritable(currentColumn++),WW.multiply(mv));

        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("otherFiles", args[2]);
        Job job = new Job(conf, "MapRed Step4");
        job.setJarByClass(HPhase4.class);
        job.setMapperClass(MyMapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        TextInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
