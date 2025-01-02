package com.tabu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.TspProblem;

public class TabuSearch {

    // 计算 TSP 的路径长度
    public static int calculateDistance(int[][] dist, int[] solution) {
        int distance = 0;
        for (int i = 0; i < solution.length - 1; i++) {
            distance += dist[solution[i]][solution[i + 1]];
        }
        distance += dist[solution[solution.length - 1]][solution[0]]; // 回到起点
        return distance;
    }

    // 生成初始解
    public static int[] generateInitialSolution(int n) {
        int[] solution = new int[n];
        for (int i = 0; i < n; i++) {
            solution[i] = i;
        }
        shuffle(solution);
        return solution;
    }

    // 交换数组中两个元素的位置
    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // 随机打乱数组
    public static void shuffle(int[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            int index = random.nextInt(array.length);
            swap(array, i, index);
        }
    }

    // 生成邻域解
    public static List<int[]> generateNeighbors(int[] solution) {
        List<int[]> neighbors = new ArrayList<>();
        for (int i = 0; i < solution.length; i++) {
            for (int j = i + 1; j < solution.length; j++) {
                int[] neighbor = solution.clone();
                swap(neighbor, i, j);
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    // 禁忌搜索算法
    public static int[] tabuSearch(TspProblem tspProblem, int tabuSize, int maxIterations) {
        int n = tspProblem.getDist().length;
        int[] bestSolution = generateInitialSolution(n);
        int bestDistance = calculateDistance(tspProblem.getDist(), bestSolution);
        int[] currentSolution = bestSolution.clone();
        int[] tabuList = new int[tabuSize];
        Arrays.fill(tabuList, -1);
        int tabuIndex = 0;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<int[]> neighbors = generateNeighbors(currentSolution);
            int[] bestNeighbor = null;
            int bestNeighborDistance = Integer.MAX_VALUE;

            for (int[] neighbor : neighbors) {
                int neighborDistance = calculateDistance(tspProblem.getDist(), neighbor);
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
                tabuIndex = (tabuIndex + 1) % tabuSize;
                if (bestNeighborDistance < bestDistance) {
                    bestSolution = bestNeighbor;
                    bestDistance = bestNeighborDistance;
                }
            }
        }
        return bestSolution;
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
        TspProblem tspProblem = new TspProblem(TspProblem.read("src\\main\\resources\\eil51.txt", 51));
        int tabuSize = 1000;
        int maxIterations = 1000;
        int[] bestSolution = tabuSearch(tspProblem, tabuSize, maxIterations);
        System.out.println("Best solution: " + Arrays.toString(bestSolution));
        System.out.println("Best distance: " + calculateDistance(tspProblem.getDist(), bestSolution));
    }
}
