## 分区总结：<br>

MapReduce执行顺序：<br>
map-->shuffle-->reduce

定义一个分区类，继承Partitioner，其泛型为Mapper的输出类型，KV形式，在自定义的分区类中重写getPartition方法，在其中实现自定义的分区思路。

<br>
在Driver中定义如下代码，让程序走自定义的分区策略：

```java
job.setPartitionerClass(FlowPartitioner.class);
job.setNumReduceTasks(3);
```

- 当ReduceTask个数 > 设置的分区数时，会生成几个空文档
- 当ReduceTask个数 < 设置的分区数，抛出IO异常
- 当ReduceTask个数是1，即使在Driver中设置了自定义分区的联系，也不会分区，而是生成一个文档

**样例**：<br>
将统计结果按照手机归属地不同省份输出到不同文件中（分区）<br>
**输入数据**：[ClickHere](https://github.com/2402575933/Hadoop/blob/main/example/partitions/data.txt)
<br>**要求**:手机号136、137、138、139开头都分别放到一个独立的4个文件中，其他开头的放到一个文件中。
<br>**代码**：[ClickHere](https://github.com/2402575933/Hadoop/tree/main/example/partitions)
