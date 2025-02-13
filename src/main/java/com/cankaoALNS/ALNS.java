package com.cankaoALNS;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;
import com.TspSolver;

/*  自适应大领域搜索求解TSP
**  Create by: WSKH
    Date:2021-07-28
    Time:21:21
*/
public class ALNS implements TspSolver {
    // 城市坐标<[x,y]>
    List<double[]> locationList;
    // 距离矩阵
    public double[][] dist;
    // 城市数量
    int cityNum;
    public final int MAX_GEN = 100000;// 最大的迭代次数(提高这个值可以稳定地提高解质量，但是会增加求解时间)
    public int[] tempGh;// 存放临时编码
    public int[] currGh;// 存放当前编码
    public int[] bestGh;// 最好的路径编码
    public int bestT;// 最佳的迭代次数
    public double tempEvaluation;// 临时解
    public double currEvaluation;// 当前解
    public double bestEvaluation;// 最优解

    public int t;// 当前迭代次数
    public Random random;// 随机函数对象
    public double T = 100; // 模拟退火温度
    public double a = 0.9; // 降温速度
    public double ro = 0.6; // 权重更新系数，控制权重变化速度

    public double[] weights; // 权重数组
    public double[] rates; // 累加的概率数组
    public int[] countArray; // 算子使用次数
    public double[] score; // 分值数组
    public int repairIndex = -1; // 修复算子索引
    public int breakIndex = -1; // 破坏算子索引

    public int N = 100; // 领域搜索次数限制
    public int[][] tabuList; // 禁忌表
    public int tabuListLen = 20; // 禁忌长度
    public int currTabuLen = 0; // 当前禁忌表长度

    private TspProblem tspProblem;

    // 如果临时解优于最优解时的得分
    public double score1 = 1.5;
    // 如果临时解优于当前解的得分
    public double score2 = 1.2;
    // 如果满足模拟退火算法Metropolis准则的得分
    public double score3 = 0.8;
    // 如果以上都没有满足时的得分
    public double score4 = 0.1;

    public TspPlan plan;

    public static void main(String[] args) throws IOException {
        TspProblem tspProblem = TSPUtils.read("src\\main\\resources\\tsp\\25Nodes\\p01.txt");
        ALNS alns_tsp = new ALNS(tspProblem);
        TspPlan plan = alns_tsp.solve();

        System.out.println(plan);

        System.out.println("Path length:" + plan.getRoute().length);

        System.out.println("Is valid solution: " + TSPUtils.isValid(plan.getRoute()));

        int[] route = new int[25];
        for (int i = 0; i < 25; i++) {
            route[i] = plan.getRoute()[i];
        }
        TspPlan plan2 = new TspPlan(route, TSPUtils.cost(route, tspProblem.getDist()), plan.getCPUtime());

        System.out.println("TspProblem.getDist():" + Arrays.toString(tspProblem.getDist()[0]));

        System.out.println("dist:" + Arrays.toString(alns_tsp.dist[0]));

        System.out.println(plan2);

        System.out.println("Path length:" + plan2.getRoute().length);

        System.out.println("Is valid solution: " + TSPUtils.isValid(plan.getRoute()));
    }

    public ALNS(TspProblem tspProblem) {
        this.locationList = new ArrayList<>();
        for (int i = 0; i < tspProblem.getCityNum(); i++) {
            double[] location = { tspProblem.getxCoors()[i], tspProblem.getyCoors()[i] };
            locationList.add(location);
        }
        this.tspProblem = tspProblem;
    }

    public ALNS(List<double[]> locationList) {
        this.locationList = locationList;
    }

    public TspPlan solve() {
        initVar();
        solver();
        return this.plan;
    }

    public void solver() {
        long startTime = System.currentTimeMillis();
        while (t <= MAX_GEN) {
            int n = 0;
            while (n <= N) {
                // 根据权重概率随机破坏，贪婪插入
                // tempGh = randomBreakAndRepair(currGh.clone());
                // 根据权重概率随机破坏和修复算子
                tempGh = randomBreakAndRepair2(currGh.clone());
                if (!isInTabuList(tempGh)) {
                    t++;
                    tempEvaluation = evaluate(tempGh);
                    if (tempEvaluation < bestEvaluation) {
                        // 如果临时解优于最优解
                        bestEvaluation = tempEvaluation;
                        bestGh = tempGh.clone();
                        // 加分
                        score[breakIndex] += score1;
                        score[repairIndex] += score1;
                        //
                        bestT = t;
                    } else if (tempEvaluation < currEvaluation) {
                        // 如果临时解优于当前解
                        currEvaluation = tempEvaluation;
                        currGh = tempGh.clone();
                        // 加分
                        score[breakIndex] += score2;
                        score[repairIndex] += score2;
                    } else {
                        // 如果临时解比全局最优解和当前解都差
                        double r = random.nextDouble();
                        if (r <= Math.exp(-1 * (Math.abs(tempEvaluation - currEvaluation) / T))) {
                            // 如果满足模拟退火算法Metropolis准则，那么临时解替换当前解
                            currEvaluation = tempEvaluation;
                            currGh = tempGh.clone();
                            // 加分
                            score[breakIndex] += score3;
                            score[repairIndex] += score3;
                        } else {
                            // 如果没有满足
                            score[breakIndex] += score4;
                            score[repairIndex] += score4;
                        }
                    }
                    break;
                } else {
                    // 如果在禁忌表中，则此次搜索不算
                    n++;
                }
                // 加入禁忌表
                enterTabuList(tempGh.clone());
                // 更新概率
                rates = updateRates();
                // 降温
                T = T * (1.0 - a);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("最佳迭代次数:" + bestT);
        System.out.println("最短路程为：" + bestEvaluation);
        int[] bestPath = new int[cityNum];
        System.arraycopy(bestGh, 0, bestPath, 0, bestGh.length);
        System.out.println("最佳路径为：" + Arrays.toString(bestPath));
        System.out.println("costFunc：" + TSPUtils.cost(bestPath, tspProblem.getDist()));
        plan = new TspPlan(bestPath, (int) bestEvaluation, (endTime - startTime) / 1000.0);
    }

    // 插入禁忌表
    public void enterTabuList(int[] tempGh) {
        if (currTabuLen < tabuListLen) {
            // 如果当前禁忌表还有空位，则直接加入即可
            tabuList[currTabuLen] = tempGh.clone();
            currTabuLen++;
        } else {
            // 如果禁忌表已经满了，则移除第一个进表的路径，添加新的路径到禁忌表末尾
            // 后面的禁忌编码全部向前移动一位，覆盖掉当前第一个禁忌编码
            for (int i = 0; i < tabuList.length - 1; i++) {
                tabuList[i] = tabuList[i + 1].clone();
            }
            // 将tempGh加入到禁忌队列的最后
            tabuList[tabuList.length - 1] = tempGh.clone();
        }
    }

    // 判断是否存在于禁忌表
    public boolean isInTabuList(int[] tempGh) {
        int count = 0;
        for (int[] ints : tabuList) {
            for (int j = 0; j < ints.length; j++) {
                if (tempGh[j] != ints[j]) {
                    count++;
                    break;
                }
            }
        }
        return count != tabuList.length;
    }

    // 根据权重概率随机破坏和修复算子
    public int[] randomBreakAndRepair2(int[] currGh) {
        double r = random.nextDouble();
        // 破坏算子
        breakIndex = -1;
        for (int i = 0; i < rates.length; i++) {
            if (i == rates.length - 1 || (r > rates[i] && r <= rates[i + 1])) {
                breakIndex = i;
                break;
            }
        }
        // 修复算子
        repairIndex = -1;
        r = random.nextDouble();
        for (int i = 0; i < rates.length; i++) {
            if (i == rates.length - 1 || (r > rates[i] && r <= rates[i + 1])) {
                repairIndex = i;
                break;
            }
        }
        if (repairIndex == breakIndex) {
            countArray[repairIndex] += 1;
            return currGh.clone();
        }
        countArray[repairIndex] += 1;
        countArray[breakIndex] += 1;
        // 破坏并修复
        int breakValue = currGh[breakIndex];
        LinkedBlockingDeque<Integer> linkedBlockingDeque = new LinkedBlockingDeque<>(cityNum); // 存没被破坏的算子
        for (int i = 0; i < cityNum; i++) {
            if (i != breakIndex) {
                linkedBlockingDeque.add(currGh[i]);
            }
        }
        int[] Gh = new int[cityNum];
        for (int i = 0; i < cityNum; i++) {
            if (i != repairIndex) {
                Gh[i] = linkedBlockingDeque.poll();
            } else {
                Gh[i] = breakValue;
            }
        }
        return Gh.clone();
    }

    // 根据权重概率随机破坏，贪婪插入
    public int[] randomBreakAndRepair(int[] currGh) {
        double r = random.nextDouble();
        // 破坏算子
        breakIndex = -1;
        for (int i = 1; i < rates.length; i++) {
            if (r > rates[i - 1] && r <= rates[i]) {
                breakIndex = i;
                break;
            }
        }
        // 贪婪寻找修复算子
        double min = Double.MAX_VALUE;
        for (int i = 1; i < cityNum; i++) {
            if (i != cityNum - 1) {
                double d1 = getDistance(locationList.get(i - 1), locationList.get(i));
                double d2 = getDistance(locationList.get(i), locationList.get(i + 1));
                if (d1 + d2 < min) {
                    min = d1 + d2;
                    repairIndex = i;
                }
            } else {
                double d1 = getDistance(locationList.get(i - 1), locationList.get(i));
                double d2 = getDistance(locationList.get(i), locationList.get(0));
                if (d1 + d2 < min) {
                    min = d1 + d2;
                    repairIndex = i;
                }
            }
        }
        if (repairIndex == breakIndex) {
            countArray[repairIndex] += 1;
            return currGh;
        }
        countArray[repairIndex] += 1;
        countArray[breakIndex] += 1;
        // 破坏并修复
        int breakValue = currGh[breakIndex];
        LinkedBlockingDeque<Integer> linkedBlockingDeque = new LinkedBlockingDeque<>(cityNum); // 存没被破坏的算子
        for (int i = 0; i < cityNum; i++) {
            if (i != breakIndex) {
                linkedBlockingDeque.add(currGh[i]);
            }
        }
        int[] Gh = new int[cityNum];
        for (int i = 0; i < cityNum; i++) {
            if (i != repairIndex && !linkedBlockingDeque.isEmpty()) {
                Gh[i] = linkedBlockingDeque.poll();
            } else {
                Gh[i] = breakValue;
            }
        }
        return Gh.clone();
    }

    // 更新累加概率数组
    public double[] updateRates() {
        // 更新权重
        for (int i = 0; i < cityNum; i++) {
            if (countArray[i] != 0) {
                weights[i] = (1 - ro) * weights[i] + ro * score[i] / countArray[i];
            }
        }
        double sum = Arrays.stream(weights).sum();
        double[] rates = new double[cityNum];
        double[] r = new double[cityNum];
        for (int i = 0; i < cityNum; i++) {
            r[i] = weights[i] / sum;
        }
        double temp = 0.0;
        for (int i = 0; i < cityNum; i++) {
            temp += r[i];
            rates[i] = temp;
        }
        return rates.clone();
    }

    // 初始化变量
    public void initVar() {
        cityNum = locationList.size();// 城市数量为点的数量
        bestGh = new int[cityNum];// 最好的路径编码
        currGh = new int[cityNum];// 当前编码
        tempGh = new int[cityNum];// 存放临时编码
        dist = new double[cityNum][cityNum];// 距离矩阵
        weights = new double[cityNum];// 权重数组
        rates = new double[cityNum];
        countArray = new int[cityNum];
        score = new double[cityNum];
        tabuList = new int[tabuListLen][cityNum];
        random = new Random(System.currentTimeMillis());
        // 初始化权重数组和分值数组
        for (int i = 0; i < cityNum; i++) {
            weights[i] = 1;
            score[i] = 1;
        }
        // 更新累加的概率数组
        rates = updateRates();
        // 初始化距离矩阵
        for (int i = 0; i < dist.length; i++) {
            for (int j = i; j < dist[i].length; j++) {
                if (i == j) {
                    // 对角线为0
                    dist[i][j] = tspProblem.getDist()[i][j];
                } else {
                    // 计算i到j的距离
                    dist[i][j] = tspProblem.getDist()[i][j];
                    dist[j][i] = tspProblem.getDist()[j][i];
                }
            }
        }
        // 初始化参数
        bestT = 0;
        t = 0;
        random = new Random(System.currentTimeMillis());
        // 随机创造初始解
        currGh[0] = 0;
        List<Integer> pathList = new ArrayList<>();
        pathList.add(0);
        int index = 1;
        while (index < cityNum) {
            int r1 = random.nextInt(cityNum);
            if (!pathList.contains(r1)) {
                currGh[index++] = r1;
                pathList.add(r1);
            }
        }
        System.out.println("初始解为(" + TSPUtils.cost(currGh, tspProblem.getDist()) + ")：" + Arrays.toString(currGh));
        // 复制当前路径编码给最优路径编码
        tempGh = currGh.clone();
        bestGh = currGh.clone();
        currEvaluation = evaluate(currGh);
        bestEvaluation = currEvaluation;
        tempEvaluation = currEvaluation;
        int[] randomBreakAndRepair = randomBreakAndRepair(currGh.clone());
        System.out.println(
                "随机破坏&修复1(" + TSPUtils.cost(randomBreakAndRepair, tspProblem.getDist()) + ")："
                        + Arrays.toString(randomBreakAndRepair));
        randomBreakAndRepair = randomBreakAndRepair2(currGh.clone());
        System.out.println(
                "随机破坏&修复2(" + TSPUtils.cost(randomBreakAndRepair, tspProblem.getDist()) + ")："
                        + Arrays.toString(randomBreakAndRepair));
    }

    // 计算两点之间的距离（使用伪欧氏距离，可以减少计算量）
    public double getDistance(double[] place1, double[] place2) {
        // 伪欧氏距离在根号内除以了一个10
        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)) / 10.0);
        // return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] -
        // place2[1], 2)));
    }

    // 评价函数
    public double evaluate(int[] path) {
        double pathLen = 0.0;
        for (int i = 1; i < path.length; i++) {
            // 起点到终点途径路程累加
            pathLen += dist[path[i - 1]][path[i]];
        }
        // 然后再加上返回起点的路程
        pathLen += dist[path[path.length - 1]][path[0]];
        return pathLen;

        // double sum = 0;
        // for (int i = 0; i < path.length - 1; i++) {
        // sum += dist[path[i]][path[i + 1]];
        // }
        // sum += dist[path[path.length - 1]][path[0]];
        // return sum;
    }
}
