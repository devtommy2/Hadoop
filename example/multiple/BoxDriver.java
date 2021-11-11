package com.tommy.box007;

import com.tommy.box006.Box006Driver;
import com.tommy.box006.Box006Mapper;
import com.tommy.box006.Box006Reducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class Box007Driver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        job.setJarByClass(Box007Driver.class);

        job.setMapperClass(Box007Mapper.class);
        job.setReducerClass(Box007Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        MultipleOutputs.addNamedOutput(job, "box007", TextOutputFormat.class, Text.class, NullWritable.class);

        FileInputFormat.setInputPaths(job, new Path("C:\\Users\\TOMMY\\Desktop\\countryIp.txt"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\TOMMY\\Desktop\\output"));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
