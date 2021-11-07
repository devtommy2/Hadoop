package com.tommy.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

// 读入的一行数据调用一次map方法  输入泛型为改行数据的偏移量和该行数据的本体，wordcount需要对数据做切分
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private Text outK = new Text();
    private IntWritable outV = new IntWritable(1);
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String line = value.toString();
        String[] words = line.split("\\s+");
        for (String word : words) {
          // 注意输出的数据形式不是String而是序列化类型Text，为了防止资源的浪费提前声明全局变量Text并set好对应的值
            outK.set(word);
          // 调用对应的方法实现向reduce传送数据
            context.write(outK, outV);
        }
    }
}
