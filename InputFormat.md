## 切片机制：<br>
切片是逻辑存储，并非真实的对数据进行物理切割，默认的切片大小是块大小，每个文件单独切片无关联，不考虑数据集整体。

## FileInputFormat实现类（常用）：
```java
TextInputFormat
CombineTextInputFormat
```
### TextInputFormat:
默认的FileInputFormat实现类，如果不定义就是他。按行读取每条数据，K是该行数据在整个文件中的起始位置偏移量，V是该行数据，类型分别是`LongWritable`和`Text`

### CombineTextInputFormat：
适合处理含有大量小文件的情况，因为TextInputFormat不能对小文件有较好的处理方式而只能为每个分配一个切片来让一个MapTask执行，较为耗费资源，所以CombineTextInputFormat能按照他的规则对文件进行合并
从而让多个小文件交给一个MapTask进行处理。<br>
应用场景：处理含有多个小文件的情况。<br>
切片机制：
- 虚拟存储：将输入目录下所有文件大小，依次和设置的setMaxInputSplitSize值比较，如果不大于设置的最大值，逻辑上划分一个块。如果输入文件大于设置的最大值且大于两倍，那么以最大值切割一块；当剩余数据大小超过设置的最大值且不大于最大值2倍，此时将文件均分成2个虚拟存储块（防止出现太小切片）。例如setMaxInputSplitSize值为4M，输入文件大小为8.02M，则先逻辑上分成一个4M。剩余的大小为4.02M，如果按照4M逻辑划分，就会出现0.02M的小的虚拟存储文件，所以将剩余的4.02M文件切分成（2.01M和2.01M）两个文件。
- 切片：<br>
  1. 判断虚拟存储的文件大小是否大于setMaxInputSplitSize值，大于等于则单独形成一个切片。
  2. 如果不大于则跟下一个虚拟存储文件进行合并，共同形成一个切片。
  3. 测试举例：有4个小文件大小分别为1.7M、5.1M、3.4M以及6.8M这四个小文件，则虚拟存储之后形成6个文件块，大小分别为：
1.7M，（2.55M、2.55M），3.4M以及（3.4M、3.4M）
最终会形成3个切片，大小分别为：
（1.7+2.55）M，（2.55+3.4）M，（3.4+3.4）M
<br>
**如何使用：**
<br>
在驱动类中加入下面的代码：

```java

// 如果不设置InputFormat，它默认用的是TextInputFormat.class
job.setInputFormatClass(CombineTextInputFormat.class);

//虚拟存储切片最大值设置4m
CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);

```
完成之后直接运行程序会发现结果和原来有区别了。

**总结**

<br>
- 默认是TextInputFormat按行读取
- 使用CombineTextInputFormat进行处理小文件的情况
