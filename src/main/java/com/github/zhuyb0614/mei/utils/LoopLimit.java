package com.github.zhuyb0614.mei.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.Consumer;

/**
 * 大数据分片处理工具类
 *
 * @author zhuyunbo
 */
@UtilityClass
public class LoopLimit {

    public static <T> void loop(int partSize, List<T> list, Consumer<List<T>> consumer) {
        for (int i = 0; i < list.size(); i += partSize) {
            int toIndex = Math.min(i + partSize, list.size());
            List<T> partList = list.subList(i, toIndex);
            consumer.accept(partList);
        }
    }

}
