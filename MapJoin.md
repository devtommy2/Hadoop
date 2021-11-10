# Map Join
能有效的缓解Reduce端的压力，将数据量尽量的在Map端做处理，传到reduce端的数据尽可能的少些，资源分配更合理，防止出现数据倾斜，资源占用不均等情况。<br>

**具体措施：** 
- 在Mapper的setup阶段，将文件读取到缓存集合中。
- 在Driver驱动类中加载缓存：

```java
//缓存普通文件到Task运行节点。
job.addCacheFile(new URI("file:///e:/cache/pd.txt"));
//如果是集群运行,需要设置HDFS路径
job.addCacheFile(new URI("hdfs://hadoop102:8020/cache/pd.txt"));
```

## 案例：

|id|pid|amount|
|---|---|---|
|1001|	01|	1
|1002|	02|	2
|1003|	03|	3
|1004|	01|	4
|1005|	02|	5
|1006|	03|	6

|pid|pname|
|---|---|
|	01|小米
|	02|华为
|	03|格力

表连接的最终格式：
|id|pid|amount|
|---|---|---|
|1001|	小米|	1
|1002|小米|	2
|1003|华为|	3
|1004|华为|	4
|1005|格力|	5
|1006|格力|	6

适合一张小表和一张大表的情形，因为小表用缓存存取，过大容易崩。
<br>

Mapper文件：

```java
package com.tommy.mapJoin;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

public class MapJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

    private HashMap<String, String> pdMap = new HashMap<>();
    private Text outK = new Text();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // 获取缓存的文件，并把文件内容封装到集合   pd.txt
        URI[] cacheFiles = context.getCacheFiles();
        FileSystem fileSystem = FileSystem.get(context.getConfiguration());
        FSDataInputStream fis = fileSystem.open(new Path(cacheFiles[0]));
        // 从流中读取数据
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
        String line;
        while (!StringUtils.isEmpty(line=bufferedReader.readLine())) {
            String[] split = line.split("\t");
            pdMap.put(split[0], split[1]);
            System.out.println(Arrays.toString(split));
        }
        IOUtils.closeStream(bufferedReader);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 处理order.txt
        String line = value.toString();
        String[] split = line.split("\t");
        // 获取pname
        String pname = pdMap.get(split[1]);
        outK.set(split[0] + "\t" + pname + "\t" + split[2]);
        context.write(outK, NullWritable.get());
    }
}

```

将缓存中的数据直接存储在HashMap中（初始化方法），然后在map方法中拼接字符串，封装后直接输出context.write()。

Driver方法：

```java
package com.tommy.mapJoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MapJoinDriver {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ClassNotFoundException {

        // 1 获取job信息
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        // 2 设置加载jar包路径
        job.setJarByClass(MapJoinDriver.class);

        // 3 关联mapper
        job.setMapperClass(MapJoinMapper.class);

        // 4 设置Map输出KV类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        // 5 设置最终输出KV类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        // 加载缓存数据
        job.addCacheFile(new URI("file:///C:/Users/TOMMY/Desktop/input/pd.txt"));

        // Map端Join的逻辑不需要Reduce阶段，设置reduceTask数量为0
        job.setNumReduceTasks(0);

        // 6 设置输入输出路径
        FileInputFormat.setInputPaths(job, new Path("C:\\Users\\TOMMY\\Desktop\\input\\order.txt"));
        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\TOMMY\\Desktop\\output"));

        // 7 提交
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}

```
