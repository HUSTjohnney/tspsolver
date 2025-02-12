package com.alns;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;
import com.TspSolver;
import com.sa.SA;

/*  自适应大领域搜索求解TSP
**  Create by: WSKH
    Date:2021-07-28
    Time:21:21
*/
public class ALNS implements TspSolver {
    // 城市数量
    int cityNum;
    public final int MAX_GEN = 100000; // 10w 最大的迭代次数(提高这个值可以稳定地提高解质量，但是会增加求解时间)
    public int[] tempRoute; // 存放临时编码
    public int[] currRoute; // 存放当前编码
    public int[] bestRoute; // 最好的路径编码
    public int bestT; // 最佳的迭代次数
    public double tempEvaluation; // 临时解
    public double currEvaluation; // 当前解
    public double bestEvaluation; // 最优解

    public int t; // 当前迭代次数

    public Random random; // 随机函数对象
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

    // 如果临时解优于最优解时的得分
    public double score1 = 1.5;
    // 如果临时解优于当前解的得分
    public double score2 = 1.2;
    // 如果满足模拟退火算法Metropolis准则的得分
    public double score3 = 0.8;
    // 如果以上都没有满足时的得分
    public double score4 = 0.1;

    private TspProblem problem;
    private List<int[]> locationList;

    // 记录每轮迭代的最优解
    private List<Double> iterationBestEvaluations = new ArrayList<>();

    public ALNS(TspProblem tspProblem) {
        this.locationList = new ArrayList<>();
        for (int i = 0; i < tspProblem.getCityNum(); i++) {
            int[] location = new int[2];
            location[0] = tspProblem.getxCoors()[i];
            location[1] = tspProblem.getyCoors()[i];
            locationList.add(location);
        }
        this.problem = tspProblem;
    }

    public static void main(String[] args) throws IOException {
        TspProblem tspProblem = TSPUtils.read("src\\main\\resources\\tsp\\50Nodes\\p01.txt");
        ALNS alns_tsp = new ALNS(tspProblem);

        TspPlan p = alns_tsp.solve();

        System.out.println("ALNS: " + p);

        System.out.println("最佳路径为：" + TspPlan.print(p.getRoute()));

        System.out.println("最佳路径长度为：" + p.getCost());

        // 绘制收敛曲线
        XYSeries series = new XYSeries("ALNS_TSP");
        for (int i = 0; i < alns_tsp.iterationBestEvaluations.size(); i++) {
            series.add(i, alns_tsp.iterationBestEvaluations.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "ALNS_TSP",
                "iteration times",
                "best evaluation",
                dataset);

        XYPlot plot = chart.getXYPlot();
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setRange(0, alns_tsp.iterationBestEvaluations.size()); // 设置 X 轴范围

        ValueAxis rangeAxis = plot.getRangeAxis();
        double minValue = Collections.min(alns_tsp.iterationBestEvaluations);
        double maxValue = Collections.max(alns_tsp.iterationBestEvaluations);
        rangeAxis.setRange(minValue - 1, maxValue + 1); // 设置 Y 轴范围

        ChartFrame frame = new ChartFrame("ALNS_TSP", chart);
        frame.pack();
        frame.setVisible(true);
    }

    public TspPlan solve() {
        long startTime = System.currentTimeMillis();
        initVar();
        solver();
        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000.0;
        return new TspPlan(bestRoute, (int) bestEvaluation, duration);
    }

    public void solver() {
        while (t <= MAX_GEN) {
            int n = 0;
            while (n <= N) {
                // if (t % 1000 == 0) {
                // System.out.println("第" + t + "次迭代，第" + n + "次领域搜索，最优路径长度为：" +
                // bestEvaluation);
                // }
                // 记录每轮迭代的最优解
                this.iterationBestEvaluations.add(bestEvaluation);

                // 根据权重概率随机破坏和修复算子
                if (t <= MAX_GEN * 0.25 && t % 2 == 0) {
                    tempRoute = randomBreakAndRandomRepair(currRoute.clone());
                } else {
                    tempRoute = randomBreakAndGreedyRepair(currRoute.clone());
                }
                // tempRoute = randomBreakAndRandomRepair(currRoute.clone());
                // tempRoute = randomBreakAndGreedyRepair(currRoute.clone());
                if (!isInTabuList(tempRoute)) {
                    t++;
                    tempEvaluation = evaluate(tempRoute);
                    if (tempEvaluation < bestEvaluation) {
                        // 如果临时解优于最优解
                        bestEvaluation = tempEvaluation;
                        bestRoute = tempRoute.clone();
                        // 加分
                        score[breakIndex] += score1;
                        score[repairIndex] += score1;
                        //
                        bestT = t;
                    } else if (tempEvaluation < currEvaluation) {
                        // 如果临时解优于当前解
                        currEvaluation = tempEvaluation;
                        currRoute = tempRoute.clone();
                        // 加分
                        score[breakIndex] += score2;
                        score[repairIndex] += score2;
                    } else {
                        // 如果临时解比全局最优解和当前解都差
                        double r = random.nextDouble();
                        if (r <= Math.exp(-1 * (Math.abs(tempEvaluation - currEvaluation) / T))) {
                            // 如果满足模拟退火算法Metropolis准则，那么临时解替换当前解
                            currEvaluation = tempEvaluation;
                            currRoute = tempRoute.clone();
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
                enterTabuList(tempRoute.clone());
                // 更新概率
                rates = updateRates();
                // 降温
                T = T * (1.0 - a);
            }
        }
        System.out.println("最佳迭代次数:" + bestT);
        // System.out.println("最短路程为：" + bestEvaluation);
        int[] bestPath = new int[cityNum + 1];
        System.arraycopy(bestRoute, 0, bestPath, 0, bestRoute.length);
        bestPath[cityNum] = bestPath[0];
        // System.out.println("最佳路径为：" + Arrays.toString(bestPath));

        int[] newpath = new int[cityNum];
        for (int i = 0; i < bestRoute.length; i++) {
            newpath[i] = bestRoute[i];
        }
        // System.out.println("新的路径为：" + Arrays.toString(newpath));
        // System.out.println(TSPUtils.cost(newpath, problem.getDist()));
    }

    // 插入禁忌表
    public void enterTabuList(int[] tempRoute) {
        if (currTabuLen < tabuListLen) {
            // 如果当前禁忌表还有空位，则直接加入即可
            tabuList[currTabuLen] = tempRoute.clone();
            currTabuLen++;
        } else {
            // 如果禁忌表已经满了，则移除第一个进表的路径，添加新的路径到禁忌表末尾
            // 后面的禁忌编码全部向前移动一位，覆盖掉当前第一个禁忌编码
            for (int i = 0; i < tabuList.length - 1; i++) {
                tabuList[i] = tabuList[i + 1].clone();
            }
            // 将tempGh加入到禁忌队列的最后
            tabuList[tabuList.length - 1] = tempRoute.clone();
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
    public int[] randomBreakAndRandomRepair(int[] currRoute) {
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
            return currRoute.clone();
        }
        countArray[repairIndex] += 1;
        countArray[breakIndex] += 1;
        // 破坏并修复
        int breakValue = currRoute[breakIndex];
        LinkedBlockingDeque<Integer> linkedBlockingDeque = new LinkedBlockingDeque<>(cityNum); // 存没被破坏的算子
        for (int i = 0; i < cityNum; i++) {
            if (i != breakIndex) {
                linkedBlockingDeque.add(currRoute[i]);
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
    public int[] randomBreakAndGreedyRepair(int[] currRoute) {
        double r = random.nextDouble();
        // 破坏算子
        breakIndex = -1;
        for (int i = 1; i < rates.length; i++) {
            if (i == rates.length - 1 || r > rates[i - 1] && r <= rates[i]) {
                breakIndex = i;
                break;
            }
        }

        // 贪婪寻找修复位置
        int breakValue = currRoute[breakIndex];
        int bestInsertIndex = -1;
        double minDistance = Double.MAX_VALUE;

        // 尝试将破坏的城市插入到每个可能的位置
        for (int insertIndex = 0; insertIndex < cityNum; insertIndex++) {
            if (insertIndex == breakIndex) {
                continue;
            }
            int[] newRoute = new int[cityNum];
            int index = 0;
            for (int i = 0; i < cityNum; i++) {
                if (i != breakIndex) {
                    newRoute[index++] = currRoute[i];
                }
            }
            // 插入被破坏的城市
            System.arraycopy(newRoute, insertIndex, newRoute, insertIndex + 1, cityNum - insertIndex - 1);
            newRoute[insertIndex] = breakValue;

            // 计算新路径的总距离
            double newDistance = evaluate(newRoute);
            if (newDistance < minDistance) {
                minDistance = newDistance;
                bestInsertIndex = insertIndex;
            }
        }

        repairIndex = bestInsertIndex;

        if (repairIndex == breakIndex) {
            countArray[repairIndex] += 1;
            return currRoute.clone();
        }
        countArray[repairIndex] += 1;
        countArray[breakIndex] += 1;

        // 破坏并修复
        LinkedBlockingDeque<Integer> unbrokenCities = new LinkedBlockingDeque<>(cityNum);
        for (int i = 0; i < cityNum; i++) {
            if (i != breakIndex) {
                unbrokenCities.add(currRoute[i]);
            }
        }
        int[] newRoute = new int[cityNum];
        for (int i = 0; i < cityNum; i++) {
            if (i != repairIndex && !unbrokenCities.isEmpty()) {
                newRoute[i] = unbrokenCities.poll();
            } else if (i == repairIndex) {
                newRoute[i] = breakValue;
            }
        }
        return newRoute;
    }

    // 更新累加概率数组
    public double[] updateRates() {
        // 动态调整权重更新系数
        if (t % 1000 == 0) {
            ro = Math.max(0.1, ro * 0.9); // 每1000次迭代，权重更新系数减小
        }

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
        bestRoute = new int[cityNum];// 最好的路径编码
        currRoute = new int[cityNum];// 当前编码
        tempRoute = new int[cityNum];// 存放临时编码
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

        // 初始化参数
        bestT = 0;
        t = 0;
        random = new Random(System.currentTimeMillis());
        // 随机创造初始解
        currRoute[0] = 0;

        // currRoute = SA.getNearestNeborInitRoute(cityNum, problem.getDist());

        currRoute = TSPUtils.getRandomRoute(cityNum);

        System.out.println("初始解为：" + Arrays.toString(currRoute));
        System.out.println("初始解的路径长度为：" + evaluate(currRoute));
        // 复制当前路径编码给最优路径编码
        tempRoute = currRoute.clone();
        bestRoute = currRoute.clone();
        currEvaluation = evaluate(currRoute);
        bestEvaluation = currEvaluation;
        tempEvaluation = currEvaluation;
        // System.out.println("随机破坏和修复1：" +
        // Arrays.toString(randomBreakAndRandomRepair(currRoute.clone())) + "路径长度为："
        // + evaluate(randomBreakAndRandomRepair(currRoute.clone())));
    }

    // 评价函数
    public double evaluate(int[] path) {
        return TSPUtils.cost(path, problem.getDist());
    }
}