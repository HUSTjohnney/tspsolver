package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * TSP工具类，提供一些TSP问题的工具方法，无法实例成对象。
 */
public abstract class TSPUtils {

    /**
     * 判断路径是否合法，是否有重复的城市
     * 
     * @return True 如果路径合法，否则返回False
     */
    public static boolean isValid(final int[] rout) {
        int minCityIndex = Integer.MAX_VALUE;
        int maxCityIndex = Integer.MIN_VALUE;
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < rout.length; i++) {
            if (set.contains(rout[i])) {
                return false;
            }
            minCityIndex = Math.min(minCityIndex, rout[i]);
            maxCityIndex = Math.max(maxCityIndex, rout[i]);
            set.add(rout[i]);
        }
        // 起点和终点是否正确
        if (minCityIndex != 0 || maxCityIndex != rout.length - 1) {
            return false;
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
        swap(newRoute, r1, r2);

        return newRoute;
    }

    /**
     * 随机交换位置index和其他位置的位置
     * 
     * @param route 初始路径
     * @param index 位置
     * @return 交换后的路径
     */
    public static void swap(int[] route, final int index) {
        Random random = new Random();
        int rand = random.nextInt(route.length);

        while (index == rand) {
            rand = random.nextInt(route.length);
        }
        swap(route, index, rand);
    }

    /**
     * 交换数组中两个元素的位置
     * 
     * @param array 数组
     * @param i     位置i
     * @param j     位置j
     */
    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * 生成随机路径，先按次序生成，然后打乱N次
     * 
     * @param cityNum 城市数量
     * @return 随机路径
     */
    public static int[] getRandomRoute(int cityNum) {
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

    /**
     * 读取TSP问题
     * 
     * @param filename  TSP问题文件的路径及其名称
     * @param numCities 城市数量
     * @return TSP问题实例
     * @throws IOException 读取文件异常
     */
    public static TspProblem read(String filename) throws IOException {
        String strbuff; // 读取文件的缓冲区

        BufferedReader data = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename)));

        int numCities = 0;
        while ((strbuff = data.readLine()) != null) {
            if (strbuff.startsWith("DIMENSION")) {
                String[] temp = strbuff.split(":");
                numCities = Integer.valueOf(temp[1].trim());
            }
            if (!Character.isAlphabetic(strbuff.charAt(0)))
                break;
        }

        int[] x = new int[numCities]; // 城市x坐标
        int[] y = new int[numCities]; // 城市y坐标
        String[] tmp = strbuff.split(" ");
        x[0] = Integer.valueOf(tmp[1]);
        y[0] = Integer.valueOf(tmp[2]);

        for (int i = 1; i < numCities; i++) {
            strbuff = data.readLine();
            String[] strcol = strbuff.split(" ");
            x[i] = Integer.valueOf(strcol[1]);
            y[i] = Integer.valueOf(strcol[2]);
        }
        data.close();

        TspProblem problem = new TspProblem(x, y);

        return problem;
    }

}
