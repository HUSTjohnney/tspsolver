package com.pso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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