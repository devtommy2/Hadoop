package com.tommy.partitioner;

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

      // 在这里进行指定分区，让分区和job任务联合起来
        job.setPartitionerClass(FlowPartitioner.class);
      // 在这里进行设置分配的ReduceTask个数
        job.setNumReduceTasks(3);


        FileInputFormat.setInputPaths(job, new Path("C:\\Users\\TOMMY\\Desktop\\phone.txt"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\TOMMY\\Desktop\\output333"));

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);
    }
}
