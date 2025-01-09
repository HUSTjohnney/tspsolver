package com.pso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

// 注意类名必须为 Main, 不要有任何 package xxx 信息
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int a = in.nextInt();
            int b = in.nextInt();

            "ssss".toCharArray();

        }

        System.out.println(5);
    }

    public int[][] generateMatrix(int n) {

        int[] dr = { 0, 1, 0, -1 }; // 行的方向偏移量：右，下，左，上
        int[] dc = { 1, 0, -1, 0 }; // 列的方向偏移量：右，下，左，上
        int dir = 0; // 初始方向为右

        int[][] ret = new int[n][n];
        int size = n * n;
        int i = 1, col = 0, row = 0;
        ret[row][col] = i++;
        while (i < size + 1) {
            int newRow = row + dr[dir];
            int newCol = col + dc[dir];
            // 新位置未超出边界，且新位置未被占用
            if (newCol < n && newCol >= 0 && newRow < n && newRow >= 0 && ret[newRow][newCol] == 0) {
                ret[newRow][newCol] = i; // 赋值
                i++;
                col = newCol;
                row = newRow;
            } else {
                dir = (dir + 1) % 4; // 换一个方向尝试。
            }
        }
        return ret;
    }

    public void rotate(int[][] matrix) {
        // 1. 对角线反转 2. 每一行逆序

        for (int j = 0; j < matrix[0].length; j++) {
            for (int i = 0; i <= j; i++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];// 副对角线线翻转
                matrix[j][i] = temp;
            }
        }

        // 每一行逆序
        for (int i = 0; i < matrix.length; i++) {
            int mid = matrix[0].length / 2;
            for (int j = 0; j < mid; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][matrix[0].length - 1 - j];
                matrix[i][matrix[0].length - 1 - j] = temp;
            }
        }

    }

    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ret = new ArrayList<>();
        // 如果s的长度小于p的长度，直接返回空。
        if (s.length() < p.length()) {
            return ret;
        }

        // 基于滑动窗口。
        List<Character> pl = strToList(p);
        char[] c = s.toCharArray();

        for (int i = 0; i < s.length(); i++) {
            // 如果i大于p的长度，说明左侧的字符已经不在窗口内，需要添加到pl。
            if (i > p.length() && pl.size() < p.length()) {
                pl.add(c[i - p.length()]);
            }
            // 如果pl中包含c[i]，则从pl中删除c[i]，如果pl为空，说明找到了一个合法的子串。
            if (pl.contains(c[i])) {
                pl.remove((char) c[i]);
                if (pl.size() == 0) {
                    ret.add(i);
                }
            } else {
                pl = strToList(p);
            }
        }

        return ret;
    }

    public List<Character> strToList(String str) {
        List<Character> ret = new ArrayList<>();
        for (char c : str.toCharArray()) {
            ret.add(c);
        }
        return ret;
    }

    public int lengthOfLongestSubstring(String s) {
        // 是最长的不含有重复字符的串。
        int slow = 0, fast = 1;
        int ret = 0;
        while (fast < s.length()) {
            char[] array = s.toCharArray();
            Set<Integer> set = new HashSet<>(); // 仅记录Ascii码
            while (!set.contains((int) array[fast])) {
                set.add((int) array[fast]);
                fast++;
                if (fast == s.length()) {
                    break;
                }
            }
            ret = (fast - slow) > ret ? (fast - slow) : ret;
            slow = fast;
        }

        return ret;
    }

    public Boolean strEquals(String str1, String str2) {
        int num = 0;
        for (char a : str1.toCharArray()) {
            if (str2.contains(String.valueOf(a))) {
                num++;
            }
        }
        return num == str1.length() ? true : false;
    }

}