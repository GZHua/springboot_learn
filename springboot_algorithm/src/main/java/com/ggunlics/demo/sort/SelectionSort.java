package com.ggunlics.demo.sort;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 选择排序
 * <p>每遍循环将最小值放到首位</p>
 *
 * @author ggunlics
 * @date 2020/12/17 11:02
 **/
@Slf4j
public class SelectionSort<T extends Comparable<T>> extends Sort<T> {

    /**
     * 每遍循环将最小值放到首位
     *
     * @param arr 要排序的数组
     */
    @Override
    public void sort(T[] arr) {
        long start = System.nanoTime();
        log.info("before: {}", Arrays.toString(arr));
        int len = arr.length;
        // 循环次数
        int count = 0;

        for (int i = 0; i < len - 1; i++) {
            int min = i;
            for (int j = i + 1; j < len; j++) {
                if (less(arr[j], arr[min])) {
                    min = j;
                }
            }
            swap(arr, i, min);
            count++;
        }
        log.info("after: {}ns [{}/{}] {}", System.nanoTime() - start, count, len, Arrays.toString(arr));
    }

}
