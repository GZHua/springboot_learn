package com.ggunlics.demo.sort;

/**
 * 运行
 *
 * @author ggunlics
 * @date 2020/12/17 11:44
 **/
public class SortMain {
    public static void main(String[] args) {
//        SelectionSort<Integer> selectionSort = new SelectionSort<>();
//        BubbleSort<Integer> bubbleSort=new BubbleSort<>();
//        InsertionSort<Integer> insertionSort=new InsertionSort<>();
//        ShellSort<Integer> shellSort = new ShellSort<>();
//        MergeSort<Integer> mergeSort=new MergeSort<>();
        QuickSort<Integer> quickSort = new QuickSort<>();

        for (int i = 0; i < 1; i++) {
//            selectionSort.sort(arr);
//            bubbleSort.sort(bubbleSort.randomArray(20));
//            insertionSort.sort(insertionSort.randomArray(20000));
//            shellSort.sort(shellSort.randomArray(1000000));
//            mergeSort.sort(mergeSort.randomArray(10000000));
            quickSort.sort(quickSort.randomArray(100000000));
        }
    }
}
