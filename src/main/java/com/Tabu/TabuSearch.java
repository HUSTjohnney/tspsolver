package com.tabu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;
import com.TspSolver;

/**
 * 禁忌搜索用于解决 TSP 问题
 */
public class TabuSearch implements TspSolver {

    /**
     * TSP问题类
     */
    private final TspProblem tspProblem;

    /**
     * 禁忌搜索表的长度
     */
    private static int TABU_SIZE = 3000;

    /**
     * 最大迭代次数
     */
    private static int MAX_ITERATIONS = 1000;

    /**
     * 禁忌搜索算法的构造函数
     * 
     * @param p TSP问题
     */
    public TabuSearch(TspProblem p) {
        this.tspProblem = p;
    }

    /**
     * 随机打乱n次数组，每次随机交换两个元素的位置
     * 
     * @param array 数组
     */
    public static void shuffle(int[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            int index = random.nextInt(array.length);
            TSPUtils.swap(array, i, index);
        }
    }

    /**
     * 生成领域解
     * 
     * @param solution 当前解
     * @return 领域解列表
     */
    public static List<int[]> generateNeighbors(int[] solution) {
        List<int[]> neighbors = new ArrayList<>();
        for (int i = 0; i < solution.length; i++) {
            for (int j = i + 1; j < solution.length; j++) {
                int[] neighbor = solution.clone();
                TSPUtils.swap(neighbor, i, j);
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * 禁忌搜索算法
     * 
     * @return TSP方案
     */
    public TspPlan solve() {
        long startTime = System.currentTimeMillis();
        int n = tspProblem.getCityNum();
        int[] bestSolution = TSPUtils.getRandomRoute(n);
        int bestDistance = TSPUtils.cost(bestSolution, tspProblem.getDist());
        int[] currentSolution = bestSolution.clone();
        int[] tabuList = new int[TABU_SIZE];
        Arrays.fill(tabuList, -1);
        int tabuIndex = 0;

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            List<int[]> neighbors = generateNeighbors(currentSolution);
            int[] bestNeighbor = null;
            int bestNeighborDistance = Integer.MAX_VALUE;

            for (int[] neighbor : neighbors) {
                int neighborDistance = TSPUtils.cost(neighbor, tspProblem.getDist());
                int swapHash = hashSwap(currentSolution, neighbor);
                if (neighborDistance < bestNeighborDistance && !isTabu(tabuList, swapHash)) {
                    bestNeighbor = neighbor;
                    bestNeighborDistance = neighborDistance;
                }
            }

            if (bestNeighbor != null) {
                currentSolution = bestNeighbor;
                int swapHash = hashSwap(currentSolution, bestNeighbor);
                tabuList[tabuIndex] = swapHash;
                tabuIndex = (tabuIndex + 1) % TABU_SIZE;
                if (bestNeighborDistance < bestDistance) {
                    bestSolution = bestNeighbor;
                    bestDistance = bestNeighborDistance;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        return new TspPlan(bestSolution, bestDistance, (endTime - startTime) / 1000.0);
    }

    // 检查交换是否在禁忌表中
    public static boolean isTabu(int[] tabuList, int swapHash) {
        for (int tabu : tabuList) {
            if (tabu == swapHash) {
                return true;
            }
        }
        return false;
    }

    // 计算交换的哈希值
    public static int hashSwap(int[] solution, int[] neighbor) {
        int n = solution.length;
        for (int i = 0; i < n; i++) {
            if (solution[i] != neighbor[i]) {
                for (int j = i + 1; j < n; j++) {
                    if (solution[j] == neighbor[i] && solution[i] == neighbor[j]) {
                        return i * n + j;
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        TspProblem tspProblem = new TspProblem(TSPUtils.read("src\\main\\resources\\eil51.txt"));
        TabuSearch.setMAX_ITERATIONS(1000);
        TabuSearch.setTABU_SIZE(3000);
        TspPlan p = new TabuSearch(tspProblem).solve();
        System.out.println("Best solution: " + p);
    }

    // getter and setter
    public static void setTABU_SIZE(int tABU_SIZE) {
        TABU_SIZE = tABU_SIZE;
    }

    public static void setMAX_ITERATIONS(int mAX_ITERATIONS) {
        MAX_ITERATIONS = mAX_ITERATIONS;
    }

    public static int getTABU_SIZE() {
        return TABU_SIZE;
    }

    public static int getMAX_ITERATIONS() {
        return MAX_ITERATIONS;
    }

    public static String getParam() {
        return "TABU_SIZE:" + TABU_SIZE + ", MAX_ITERATIONS:" + MAX_ITERATIONS;
    }

}
