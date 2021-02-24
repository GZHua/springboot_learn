package com.ggunlics.demo.sort;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 插入排序
 * <p>从第2位开始遍历,每次遍历插入左边数组并排序</p>
 *
 * @author ggunlics
 * @date 2020/12/17 14:36
 **/
@Slf4j
public class InsertionSort<T extends Comparable<T>> extends Sort<T> {
    @Override
    public void sort(T[] arr) {
        long start = System.nanoTime();
        log.info("before: {}", Arrays.toString(arr));
        int len = arr.length;
        // 循环次数
        int count = 0;

        for (int i = 1; i < len; i++) {
            for (int j = i; j > 0 && less(arr[j], arr[j - 1]); j--) {
                swap(arr, j, j - 1);
            }
            count++;
        }
        log.info("after: {}ns [{}/{}] {}", System.nanoTime() - start, count, len, arr);
    }
}
