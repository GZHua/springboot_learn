package com.ggunlics.demo.sort;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 冒泡
 * <p>每遍循环将较小值放在左边, 一遍后最大值就会在最后</p>
 *
 * @author ggunlics
 * @date 2020/12/17 11:29
 **/
@Slf4j
public class BubbleSort<T extends Comparable<T>> extends Sort<T> {
    @Override
    public void sort(T[] arr) {
        long start = System.nanoTime();
        log.info("before: {}", Arrays.toString(arr));
        int len = arr.length;
        // 循环次数
        int count = 0;

        boolean isOver = false;
        for (int i = 0; i < len - 1 && !isOver; i++) {
            isOver = true;
            for (int j = i + 1; j < len; j++) {
                if (less(arr[j], arr[i])) {
                    isOver = false;
                    swap(arr, i, j);
                }
            }
            count++;
        }

        log.info("after: {}ns [{}/{}] {}", System.nanoTime() - start, count, len, Arrays.toString(arr));
    }
}
