package com.ggunlics.demo.sort;

import lombok.extern.slf4j.Slf4j;

/**
 * 归并排序
 * <p>将数组分为2组,再对分组继续分为2组,直至无法再分,对子分组排序,再对同级分组组合排序</p>
 *
 * @author ggunlics
 * @date 2020/12/17 16:31
 **/
@Slf4j
public class MergeSort<T extends Comparable<T>> extends Sort<T> {

    private T[] temArr;

    @Override
    public void sort(T[] arr) {
        long start = System.nanoTime();
        temArr = (T[]) new Comparable[arr.length];
        sort(arr, 0, arr.length - 1);
        log.info("after: {}ns", System.nanoTime() - start);
    }

    private void sort(T[] arr, int s, int e) {
        if (e <= s) {
            return;
        }
        int mid = s + (e - s) / 2;
        sort(arr, s, mid);
        sort(arr, mid + 1, e);
        merge(arr, s, mid, e);
    }

    private void merge(T[] nums, int s, int m, int e) {

        int i = s;
        // 将数据复制到辅助数组
        for (int k = s; k <= e; k++) {
            temArr[k] = nums[k]; // 将数据复制到辅助数组
        }


        int j = m + 1;
        for (int k = s; k <= e; k++) {
            if (i > m) {
                nums[k] = temArr[j++];

            } else if (j > e) {
                nums[k] = temArr[i++];

            } else if (temArr[i].compareTo(temArr[j]) <= 0) {
                // 先进行这一步，保证稳定性
                nums[k] = temArr[i++];
            } else {
                nums[k] = temArr[j++];
            }
        }
    }

}
