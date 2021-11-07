# MapReduce编程基础思想

**前言：**<br>
**Mapper阶段**：自定义的mapper需要继承Mapper并重写map方法，按照业务需求确定泛型，输入为固定的形式<偏移量, 数据的一行>，其中对每一行数据调用一次map方法，也就是对每一个键值对<K, V>调用一次map
方法<br><br>
**Reduce阶段**：自定义的reduce需要继承Reduce并重写reduce方法，按照业务需求确定泛型，其中输入形式为<K, V>，V是集合类型，在之前会将所有相同K的值进行对v合并，所以传过来的KV每一对都是独一无二的K，所以
对每一个相同的K调用一次reduce方法<br>
<br>
context：连接Driver，reduce，map的桥梁，通过context实现三者的衔接和交互。<br>
<br>
**Driver：**
驱动类：
```java
// 获取job

// setJarByClass()

// setMapperClass()

// setReducerClass()

// setMapOutputKeyClass()

// setMapOutputvValueClass()

// setOutputKeyClass()

// setOutputValueClass()

// FileInputFormat.setInputPaths()

// FileOutputFormat.setOutputPaths()

// submit()
```
