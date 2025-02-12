package com.tabu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;
import com.TspSolver;
import com.sa.SA;

public class TS implements TspSolver {

    /**
     * TSP问题类
     */
    private final TspProblem problem;

    /**
     * 禁忌长度
     */
    private static int TABU_TENURE = 50;

    /**
     * 最大迭代次数
     */
    private static int MAX_ITER = 100;

    /**
     * 初始路径数量
     */
    private static int INIT_ROUTENUM = 10;

    /**
     * 构造函数
     * 
     * @param problem
     */
    public TS(TspProblem problem) {
        this.problem = problem;
    }

    // 计算路径的总距离
    private static int calculateTotalDistance(List<Integer> route, int[][] distanceMatrix) {
        int totalDistance = 0;
        // 遍历路径中的每一个城市，计算相邻城市之间的距离并累加
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += distanceMatrix[route.get(i)][route.get(i + 1)];
        }
        // 加上最后一个城市回到起始城市的距离
        totalDistance += distanceMatrix[route.get(route.size() - 1)][route.get(0)];
        return totalDistance;
    }

    // 2-opt邻域操作：反转路径中的一段
    private static List<Integer> twoOpt(List<Integer> route, int i, int k) {
        List<Integer> newRoute = new ArrayList<>(route);
        // 提取从索引i到索引k的子路径
        List<Integer> subList = new ArrayList<>(newRoute.subList(i, k + 1));
        // 反转子路径
        for (int j = 0; j < subList.size(); j++) {
            newRoute.set(i + j, subList.get(subList.size() - 1 - j));
        }
        return newRoute;
    }

    private List<Integer> tabuSearch(int[][] distanceMatrix, List<List<Integer>> initialRoutes, int tabuTenure,
            int maxIterations) {
        List<Integer> globalBestRoute = null;
        double globalBestDistance = Double.MAX_VALUE;

        for (List<Integer> initialRoute : initialRoutes) {
            // 初始化最优路径，初始值为初始路径
            List<Integer> bestRoute = new ArrayList<>(initialRoute);
            // 计算初始最优路径的总距离
            double bestDistance = calculateTotalDistance(bestRoute, distanceMatrix);
            // 初始化当前路径，初始值为最优路径
            List<Integer> currentRoute = new ArrayList<>(bestRoute);
            // 计算当前路径的总距离
            double currentDistance = bestDistance;
            // 初始化禁忌表，用于记录被禁忌的操作
            int[][] tabuList = new int[distanceMatrix.length][distanceMatrix.length];

            // 迭代计数器
            int iteration = 0;
            // 开始迭代，直到达到最大迭代次数
            while (iteration < maxIterations) {
                iteration++;
                // 初始化最优候选路径为null
                List<Integer> bestCandidate = null;
                // 初始化最优候选路径的总距离为无穷大
                double bestCandidateDistance = Double.MAX_VALUE;

                // 生成邻域解并选择最优解
                // 遍历所有可能的2-opt操作
                for (int i = 1; i < distanceMatrix.length - 1; i++) {
                    for (int k = i + 1; k < distanceMatrix.length; k++) {
                        // 通过2-opt操作生成候选路径
                        List<Integer> candidateRoute = twoOpt(currentRoute, i, k);
                        // 计算候选路径的总距离
                        double candidateDistance = calculateTotalDistance(candidateRoute, distanceMatrix);
                        // 检查该操作是否不在禁忌表中或者该候选路径比当前最优路径更优
                        if (tabuList[currentRoute.get(i)][currentRoute.get(k)] == 0
                                || candidateDistance < bestDistance) {
                            // 如果该候选路径的总距离比当前最优候选路径的总距离更优
                            if (candidateDistance < bestCandidateDistance) {
                                // 更新最优候选路径
                                bestCandidate = candidateRoute;
                                // 更新最优候选路径的总距离
                                bestCandidateDistance = candidateDistance;
                            }
                        }
                    }
                }

                // 遍历所有可能的3-opt操作
                for (int i = 1; i < distanceMatrix.length - 2; i++) {
                    for (int j = i + 1; j < distanceMatrix.length - 1; j++) {
                        // k 为随机选择的大于 j 的索引
                        int k = j + 1 + (int) (Math.random() * (distanceMatrix.length - j - 1));
                        // 通过3-opt操作生成候选路径
                        List<Integer> candidateRoute = threeOpt(currentRoute, i, j, k);
                        // 计算候选路径的总距离
                        double candidateDistance = calculateTotalDistance(candidateRoute, distanceMatrix);
                        // 检查该操作是否不在禁忌表中或者该候选路径比当前最优路径更优
                        if (tabuList[currentRoute.get(i)][currentRoute.get(k)] == 0
                                || candidateDistance < bestDistance) {
                            // 如果该候选路径的总距离比当前最优候选路径的总距离更优
                            if (candidateDistance < bestCandidateDistance) {
                                // 更新最优候选路径
                                bestCandidate = candidateRoute;
                                // 更新最优候选路径的总距离
                                bestCandidateDistance = candidateDistance;
                            }
                        }

                    }
                }

                // 更新当前路径为最优候选路径
                if (bestCandidate != null) {
                    currentRoute = bestCandidate;
                    // 更新当前路径的总距离
                    currentDistance = bestCandidateDistance;

                    // 更新禁忌表
                    for (int i = 1; i < distanceMatrix.length - 1; i++) {
                        for (int k = i + 1; k < distanceMatrix.length; k++) {
                            // 将当前操作加入禁忌表，设置禁忌长度
                            tabuList[currentRoute.get(i)][currentRoute.get(k)] = tabuTenure;
                        }
                    }
                }

                // 禁忌表中所有操作的禁忌长度减1
                for (int i = 0; i < distanceMatrix.length; i++) {
                    for (int j = 0; j < distanceMatrix.length; j++) {
                        tabuList[i][j] = Math.max(tabuList[i][j] - 1, 0);
                    }
                }

                // 如果当前路径的总距离比最优路径的总距离更优
                if (currentDistance < bestDistance) {
                    // 更新最优路径为当前路径
                    bestRoute = new ArrayList<>(currentRoute);
                    // 更新最优路径的总距离
                    bestDistance = currentDistance;
                }
            }

            if (bestDistance < globalBestDistance) {
                globalBestDistance = bestDistance;
                globalBestRoute = bestRoute;
            }
        }

        return globalBestRoute;
    }

    // 3-opt邻域操作
    private static List<Integer> threeOpt(List<Integer> route, int i, int j, int k) {
        List<Integer> newRoute = new ArrayList<>(route);
        // 这里可以根据3-opt的规则对路径进行调整，具体实现根据不同的3-opt变体有所不同
        // 简单示例：可以将三个子路径进行不同的拼接组合
        List<Integer> subList1 = new ArrayList<>(newRoute.subList(0, i));
        List<Integer> subList2 = new ArrayList<>(newRoute.subList(i, j));
        List<Integer> subList3 = new ArrayList<>(newRoute.subList(j, k));
        List<Integer> subList4 = new ArrayList<>(newRoute.subList(k, newRoute.size()));

        // 这里只是一种简单的组合方式，实际应用中可能需要尝试多种组合
        newRoute.clear();
        newRoute.addAll(subList1);
        newRoute.addAll(subList3);
        newRoute.addAll(subList2);
        newRoute.addAll(subList4);

        return newRoute;
    }

    @Override
    public TspPlan solve() {
        int[][] distance = problem.getDist();
        long startTime = System.currentTimeMillis();
        List<Integer> bestPath = new ArrayList<>();
        List<List<Integer>> initialRoutes = new ArrayList<>();
        int n = distance.length;
        int[] nearestNebor = SA.getNearestNeborInitRoute(n, distance);
        List<Integer> nearestNeborlist = new ArrayList<>();
        for (int i = 0; i < nearestNebor.length; i++) {
            nearestNeborlist.add(nearestNebor[i]);
        }
        initialRoutes.add(nearestNeborlist);
        for (int i = 0; i < INIT_ROUTENUM - 1; i++) { // 生成10个不同的初始解
            List<Integer> initialRoute = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                initialRoute.add(j);
            }
            java.util.Collections.shuffle(initialRoute);
            initialRoutes.add(initialRoute);
        }

        try {
            // 调用禁忌搜索算法求解最优路径
            bestPath = tabuSearch(distance, initialRoutes, TABU_TENURE, MAX_ITER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 计算最优路径的总距离
        double bestDistance = calculateTotalDistance(bestPath, distance);
        long endTime = System.currentTimeMillis();
        return new TspPlan(bestPath, bestDistance, (endTime - startTime) / 1000.0);
    }

    public static void main(String[] args) throws IOException {
        test1();
    }

    static void test1() throws IOException {
        TspProblem p = TSPUtils.read("src\\main\\resources\\tsp\\25Nodes\\p01.txt");

        TS ts = new TS(p);
        TS.setMAX_ITER(1000);
        TS.setTABU_TENURE(200);

        TspPlan plan = ts.solve();

        System.out.println("TS: " + plan);

        System.out.println("Path length:" + plan.getRoute().length);
        System.out.println("Is valid solution: " + TSPUtils.isValid(plan.getRoute()));

    }

    // Getter and Setter
    public static void setTABU_TENURE(int tabuTenure) {
        TS.TABU_TENURE = tabuTenure;
    }

    public static void setMAX_ITER(int maxIterations) {
        TS.MAX_ITER = maxIterations;
    }

    public static void setINIT_ROUTENUM(int initRouteNum) {
        TS.INIT_ROUTENUM = initRouteNum;
    }

    public static String getParam() {
        return "TABU_TENURE: " + TABU_TENURE + ", MAX_ITER: " + MAX_ITER + ", INIT_ROUTENUM: " + INIT_ROUTENUM;
    }

}