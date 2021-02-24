package com.ggunlics.demo.sort;

import lombok.extern.slf4j.Slf4j;

/**
 * 希尔排序
 * <p>以n为间隔,将在逻辑上数组划分为m个小数组,对小数组排序,将n减半后再次划分,直至n=1</p>
 *
 * @author ggunlics
 * @date 2020/12/17 15:36
 **/
@Slf4j
public class ShellSort<T extends Comparable<T>> extends Sort<T> {
    @Override
    public void sort(T[] arr) {
        long start = System.nanoTime();
//        log.info("before: {}", Arrays.toString(arr));
        int len = arr.length;
        // 循环次数
        int count = 0;
        // 计算gap, 每遍减半
        for (int gap = len / 2; gap > 0; gap /= 2) {
            // 从中间开始从左到右计算,并按顺序执行分组
            for (int i = gap; i < len; i++) {
                int j = i;
                while (j - gap >= 0 && less(arr[j], arr[j - gap])) {
                    swap(arr, j, j - gap);
                    j -= gap;
                }
            }

            count++;
        }

//        log.info("after: {}ns [{}/{}] {}", System.nanoTime() - start,count,len, Arrays.toString(arr));
        log.info("after: {}ns [{}/{}]", System.nanoTime() - start, count, len);
    }
}
