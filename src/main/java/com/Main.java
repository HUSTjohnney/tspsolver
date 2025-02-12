package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

// 注意类名必须为 Main, 不要有任何 package xxx 信息
public class Main {
    public static void main(String[] args) {
        Main m = new Main();

        System.out.println(m.climbNum(10));
    }
    
    

    // 给出由小写字母组成的字符串 s，重复项删除操作会选择两个相邻且相同的字母，并删除它们。
    // 在 s 上反复执行重复项删除操作，直到无法继续删除。在完成所有重复项删除操作后返回最终的字符串。答案保证唯一。
    // 输入："abbaca"输出："ca"

    public String delete(String input) {
        // 1. 用栈来存储字符，如果栈顶元素和当前元素相同，则弹出栈顶元素。
        // 2. 最后将栈中的元素转换成字符串。
        char[] array = input.toCharArray();
        Stack<Character> stack = new Stack<>();
        for (char c : array) {
            if (stack.isEmpty() || stack.peek() != c) {
                stack.push(c);
            } else {
                stack.pop();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (char c : stack) {
            sb.append(c);
        }
        return sb.toString();
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

    public int lengthOfLIS(int[] nums) {
        // 暴力搜索O(n^k)其中k是返回的长度
        // 动态规划，从后往前。存储开头的数字，以及练成串的序列。
        // f(8) = {18,1};
        // f(7) = {101,1}
        // f(6) = {7,2}
        // f(5) = {3,3}
        // f(4) = {5,3}
        // f(3) = {2,4}

        int ans = 1, n = nums.length - 1;
        Map<Integer, Integer> list = new HashMap<>();
        list.put(nums[n], 1);
        for (int i = n - 1; i >= 0; i--) {
            for (int val : list.keySet()) {
                if (val < nums[i]) {
                    list.put(nums[i], Math.max(list.getOrDefault(nums[i], 1), list.get(val)));
                }
            }
        }
        // 找到最大的val
        for (int val : list.values()) {
            ans = Math.max(ans, val);
        }
        return ans;
    }

    public List<Integer> partitionLabels(String s) {
        // 最开始分配成1个区间
        // 遍历一遍，记录字母的最后出现时刻和最早出现时刻？
        Map<Character, int[]> map = new HashMap<>();
        char[] ss = s.toCharArray();
        for (int i = 0; i < ss.length; i++) {
            char c = ss[i];
            if (!map.containsKey(c)) {
                map.put(c, new int[] { i, i });
            } else {
                map.get(c)[1] = i;
            }
            System.out.println(c + "," + map.get(c)[0] + "," + map.get(c)[1]);
        }
        return null;

    }

    // 爬楼梯：每次爬一阶或者二阶，请问到第n阶有多少爬法
    // 动态规划
    public int climbNum(int num) {
        // f(0) = 0
        // f(1) = 1
        // f(2) = f(1)+1 // 2
        // f(3) = max(f(2)+1,f(1)+1) // 3 // {111 12 21}
        // f(n) = (f(n-1)+f(n-2)) // 4层 5种 {1111 121 211 112 22}

        if (num == 0) {
            return 0;
        } else if (num == 1)
            return 1;

        int[] f = new int[num];
        f[0] = 1;
        f[1] = 1;

        for (int i = 2; i < f.length; i++) {
            f[i] = f[i - 1] + f[i - 2];
            System.out.println("f[" + i + "]:" + f[i]);
        }

        return f[num - 1];
    }

    public int jump(int[] nums) {
        // 动态规划 f[当前位置] = 到当前位置所需的最小步数
        // f[0] = 0 初始位置，无需跳跃
        // f[1] = 1 if(f[0]+nums[0]>=1)
        // f[2] = min(f[0]+nums[0]>=2,f[1]+nums[1])
        // f[3] = min(f[0]+nums[0],)

        int[] f = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            f[i] = Integer.MAX_VALUE;
        }
        f[0] = 0;
        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (f[j] + nums[j] > i) {
                    f[i] = Math.min(f[i], f[j] + 1);
                }
            }
        }
        char c = "ssss".toCharArray()[2];

        return 0;
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