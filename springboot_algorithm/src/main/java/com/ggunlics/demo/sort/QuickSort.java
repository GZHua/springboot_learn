package com.ggunlics.demo.sort;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 快速排序
 *
 * @author ggunlics
 * @date 2020/12/18 9:50
 **/
@Slf4j
public class QuickSort<T extends Comparable<T>> extends Sort<T> {
    @Override
    public void sort(T[] arr) {
        long start = System.nanoTime();
//        log.info("初始: {}", Arrays.toString(arr));
        shuffle(arr);
        sort(arr, 0, arr.length - 1);
        log.info("after: {}", time(System.nanoTime() - start));
//        log.info("after: {} {}", time(System.nanoTime() - start),Arrays.toString(arr));
    }

    /**
     * 划分
     *
     * @param arr   源数据
     * @param start 起始端
     * @param end   末尾端
     */
    private void sort(T[] arr, int start, int end) {
        // 递归退出
        if (start >= end) {
            return;
        }

        T base = arr[start];
        int i = start, j = end + 1;
        while (i != j) {
            // 从右边取小于基准的
            while (less(base, arr[--j]) && i < j) {}

            // 从左边取大于基准的
            while (less(arr[++i], base) && i < j) {}

            // 当i>=j时退出
            if (i >= j) {
                break;
            } else {
                // 交换i,j位置
                swap(arr, i, j);
            }
        }
        // 基准放中间
        swap(arr, start, j);
//        log.info("交换后: 基准[{}] {}", base, Arrays.toString(arr));

        // 常规
        sort(arr, start, j - 1);
        sort(arr, j + 1, end);
    }

    /**
     * 洗牌
     *
     * @param nums
     */
    private void shuffle(T[] nums) {
        List<Comparable> list = Arrays.asList(nums);
        Collections.shuffle(list);
        list.toArray(nums);
    }

}
