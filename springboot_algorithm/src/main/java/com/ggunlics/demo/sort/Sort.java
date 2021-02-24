package com.ggunlics.demo.sort;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * 基类 - 排序
 *
 * @author ggunlics
 * @date 2020/12/17 10:55
 **/
public abstract class Sort<T extends Comparable<T>> {
    private final Random random = new Random();

    /**
     * 排序方法
     *
     * @param arr 要排序的数组
     */
    public abstract void sort(T[] arr);

    /**
     * 小于
     *
     * @param a 比较项
     * @param b 被比较项
     * @return true a&lt;b; false a>b;
     */
    protected boolean less(T a, T b) {
        return a.compareTo(b) < 0;
    }

    /**
     * 交换位置
     *
     * @param a 数组
     * @param i 位置1
     * @param j 位置2
     */
    protected void swap(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * 生成随机int数组
     *
     * @param len 数组长度
     * @return int数组
     */
    protected Integer[] randomArray(int len) {
        Integer[] arr = new Integer[len];

        for (int i = 0; i < len; i++) {
            arr[i] = random.nextInt(100);
        }
        return arr;
    }

    /**
     * 易读时间
     *
     * @param nanoTime 纳秒
     * @return 易读时间
     */
    protected String time(long nanoTime) {
        StringBuilder time = new StringBuilder();
        String[] unit = new String[]{"ns", "us", "ms", "s"};

        // 翻转顺序方便计算
        String nanoStr = StringUtils.reverse(String.valueOf(nanoTime));
        int len = nanoStr.length();
        // 进制计数
        int i = 0;
        // 单位计数
        int j = 0;
        while (len - 1 >= i && i < 12) {
            if (i + 3 <= len - 1) {
                time.append(StringUtils.reverse(nanoStr.substring(i, Math.min((i + 3), len - 1)))).append(unit[j++]).append(
                        " ");
            } else {
                time.append(StringUtils.reverse(nanoStr.substring(i))).append(unit[j++]).append(" ");
            }

            i += 3;
        }
        // 置为正常阅读时间顺序
        String[] splitTime = time.toString().split(" ");
        ArrayUtils.reverse(splitTime);

        return String.join(" ", splitTime);
    }
}
