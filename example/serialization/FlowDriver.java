package com.tommy.writable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(FlowDriver.class);

        job.setMapperClass(FlowMapper.class);
        job.setReducerClass(FlowReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

//        FileInputFormat.setInputPaths(job, new Path(args[0]));
//        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // 输出路径不能存在，无论是在hdfs还是在win本地，都不能是一个已经存在的文件夹，否则会抛出异常
        FileInputFormat.setInputPaths(job, new Path("C:\\Users\\TOMMY\\Desktop\\phone.txt"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\TOMMY\\Desktop\\output"));

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);
    }
}
