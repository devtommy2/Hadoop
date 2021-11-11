package com.tommy.box007;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class Box007Mapper extends Mapper<LongWritable, Text, Text, Text> {
    private Text outK = new Text();
    private Text outV = new Text();
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] split = line.split("\t");
        outK.set(split[1]);
        outV.set(line);
        context.write(outK, outV);
    }
}
