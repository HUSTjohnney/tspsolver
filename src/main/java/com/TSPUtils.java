package com;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * TSP工具类
 */
public class TSPUtils {
    /**
     * 判断路径是否合法，是否有重复的城市
     * 
     * @return True 如果路径合法，否则返回False
     */
    public static boolean isValid(final int[] rout) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < rout.length; i++) {
            if (set.contains(rout[i])) {
                return false;
            }
            set.add(rout[i]);
        }
        return true;
    }

    /**
     * 计算路径长度
     * 
     * @param rout 路径
     * @return 路径长度
     */
    public static int cost(final int[] rout, final int[][] dist) {
        int sum = 0;
        for (int i = 0; i < rout.length - 1; i++) {
            sum += dist[rout[i]][rout[i + 1]];
        }
        sum += dist[rout[rout.length - 1]][rout[0]];
        return sum;
    }

    /**
     * 随机交换路径中两个城市的位置
     * 
     * @param route 初始路径
     * @return 交换后的路径
     */
    public static int[] swap(final int[] route) {
        Random random = new Random();
        int r1 = random.nextInt(route.length);
        int r2 = random.nextInt(route.length);

        while (r1 == r2) {
            r2 = random.nextInt(route.length);
        }

        int[] newRoute = Arrays.copyOf(route, route.length);
        int temp = newRoute[r1];
        newRoute[r1] = newRoute[r2];
        newRoute[r2] = temp;

        return newRoute;
    }

    /**
     * 随机交换位置index和其他位置的位置
     * 
     * @param route 初始路径
     * @param index 位置
     * @return 交换后的路径
     */
    public static void swap(final int[] route, final int index) {
        Random random = new Random();
        int rand = random.nextInt(route.length);

        while (index == rand) {
            rand = random.nextInt(route.length);
        }

        int temp = route[index];
        route[index] = route[rand];
        route[rand] = temp;
    }

    /**
     * 生成随机路径
     * 
     * @param cityNum 城市数量
     * @return 随机路径
     */
    public static int[] findRandomRoute(int cityNum) {
        int[] route = new int[cityNum];
        for (int i = 0; i < cityNum; i++) {
            route[i] = i;
        }

        // 对于每个位置，都随机换个其他的位置
        for (int i = 0; i < cityNum; i++) {
            swap(route, i);
        }

        return route;
    }

}
