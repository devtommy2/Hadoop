package com.tommy.partitioner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;


// shuffle的数据来自于map，所以这里的泛型和map的输出KV的泛型一样
public class FlowPartitioner extends Partitioner<Text, FlowBean> {

    @Override
    public int getPartition(Text text, FlowBean flowBean, int i) {

        String phone = text.toString();
        String prePhone = phone.substring(0, 3);
        int partition = 0;
        if (prePhone.equals("136")) {
            partition = 0;
        } else if (prePhone.equals("137")) {
            partition = 1;
        } else if (prePhone.equals("138")) {
            partition = 2;
        } else {
            partition = 3;
        }
        return partition;
    }
}
