package com;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.Cplex.CplexSolver;
import com.aco.ACO;
import com.ga.GA;
import com.greedy.Greedy;
import com.sa.SA;

public class TSPsolveInstance {
    public static void main(String[] args) throws IOException {

        // 算例的节点数量
        int nodeNum = 50;

        // 选择算法："CPLEX"/"SA"/"Greedy"/"GA"/"ACO"
        String algorithm = "SA";
        String para = "";

        if (algorithm.equals("CPLEX")) {
            para = CplexSolver.getParam();
        } else if (algorithm.equals("SA")) {
            SA.setINIT_TEMP(1e6);
            SA.setDECRESE_RATE(0.995);
            SA.setTEMP_LB(1e-6);
            SA.setMAX_ITER_TIME(50 * nodeNum);
            para = SA.getParam();
        } else if (algorithm.equals("Greedy")) {
            para = Greedy.getParam();
        } else if (algorithm.equals("GA")) {
            GA.setCHORO_NUM(nodeNum * 10); // 种群数量
            GA.setCROSS_RATE(0.8); // 交叉率
            GA.setMUTATE_RATE(0.2); // 变异率
            GA.setELITE_RATE(0.20); // 精英保留率
            GA.setMAX_GEN(1000); // 最大迭代次数
            para = GA.getParam();
        } else if (algorithm.equals("ACO")) {
            ACO.setALPHA(1.f);
            ACO.setBETA(5.f);
            ACO.setRHO(0.5f);
            ACO.setAntNum(nodeNum / 2);
            ACO.setMAX_GEN(100);
            para = ACO.getParam();
        } else {
            System.out.println("Invalid algorithm name!");
            return;
        }

        String filePath = "src\\main\\resources\\" + nodeNum + "Nodes\\";
        // 创建 FileWriter 对象，用于写入结果到文件
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("src\\main\\resources\\results\\" + algorithm + "_" + nodeNum + ".txt"));

        // 获取当前日期和时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        // 写入具体的实验信息
        writer.write("--------------------------------------------------\n");
        writer.write("Experiment Date: " + currentTime + "\n");
        writer.write("Instance Information: " + nodeNum + " nodes " + " TSP Problems\n");
        writer.write("Algorithm: " + algorithm + "\n");
        writer.write("Parameters: " + para + "\n");
        writer.write("--------------------------------------------------\n");

        // 写入表头
        writer.write(String.format("%-10s %-10s %-10s %-10s \n", "Problem", "Cost", "Time(s)", "Mermory(MB)"));

        Double avgCost = 0.0;
        Double avgTime = 0.0;
        Double avgMemory = 0.0;

        // 读取并解决所有算例
        for (int i = 1; i <= 20; i++) {
            String fileName = "p" + (i < 10 ? ("0" + i) : i) + ".txt"; // 待读取的 STND 文件名称
            TspProblem tsp = TSPUtils.read(filePath + fileName, nodeNum); // 解析 STND
            System.out.println("Solving " + fileName + "...");

            TspPlan plan = null;

            if (algorithm.equals("CPLEX")) {
                plan = new CplexSolver(tsp).solve();
            } else if (algorithm.equals("SA")) {
                SA.setINIT_TEMP(1e5 * nodeNum);
                SA.setDECRESE_RATE(0.995);
                SA.setTEMP_LB(1e-6);
                SA.setMAX_ITER_TIME(50 * nodeNum);
                plan = new SA(tsp).solve();
            } else if (algorithm.equals("Greedy")) {
                plan = new Greedy(tsp).solve();
            } else if (algorithm.equals("GA")) {
                GA.setCHORO_NUM(nodeNum * 10); // 种群数量
                GA.setCROSS_RATE(0.9); // 交叉率
                GA.setMUTATE_RATE(0.1); // 变异率
                GA.setELITE_RATE(0.20); // 精英保留率
                GA.setMAX_GEN(1000); // 最大迭代次数
                plan = new GA(tsp).solve();
            } else if (algorithm.equals("ACO")) {
                ACO.setALPHA(1.f);
                ACO.setBETA(5.f);
                ACO.setRHO(0.5f);
                ACO.setAntNum(nodeNum / 2);
                ACO.setMAX_GEN(100);
                plan = new ACO(tsp).solve();
            }

            if (plan != null) {
                System.out.printf("solved successfully, using %.2f s\n", plan.getCPUtime());
                writer.write(String.format(
                        "%-10s %-10d %-10.2f %-10.2f \n",
                        fileName.substring(0, fileName.indexOf('.')),
                        plan.getCost(), plan.getCPUtime(), plan.getMemoryUsage()));
                avgCost += plan.getCost();
                avgTime += plan.getCPUtime();
                avgMemory += plan.getMemoryUsage();
            }
        }

        // 写入平均值
        writer.write("--------------------------------------------------\n");
        writer.write(String.format("%-10s %-10.2f %-10.2f %-10.2f \n", "Average", avgCost / 20, avgTime / 20,
                avgMemory / 20));

        // 关闭文件写入流
        writer.close();
    }
}