package com;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.Cplex.CplexSolver;
import com.ga.GA;
import com.greedy.Greedy;
import com.sa.SA;

public class TSPsolveInstance {
    public static void main(String[] args) throws IOException {

        // 算例的节点数量
        int nodeNum = 50;

        // 选择算法
        String algorithm = "CPLEX";
        // String algorithm = "SA";

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
        writer.write("--------------------------------------------------\n");

        // 写入表头
        writer.write(String.format("%-10s %-10s %-10s %-10s \n", "Problem", "Cost", "Time(s)", "Mermory(MB)"));

        for (int i = 1; i <= 20; i++) {
            String fileName = "p" + (i < 10 ? ("0" + i) : i) + ".txt"; // 待读取的 STND 文件名称
            TspProblem tsp = TspProblem.read(filePath + fileName, nodeNum); // 解析 STND
            System.out.println("Solving " + fileName + "...");

            TspPlan plan = null;

            if (algorithm.equals("CPLEX")) {
                plan = new CplexSolver(tsp).solve();
            } else if (algorithm.equals("SA")) {
                SA.setInitialTemporature(1e6);
                SA.setDecresRate(0.99);
                SA.setTemperatureLB(1e-6);
                SA.setIterTimes(50 * nodeNum);
                plan = new SA(tsp).solve();
            } else if (algorithm.equals("Greedy")) {
                // plan = new Greedy(tsp).solve();
                // TODO: 实现 Greedy 算法
            } else if (algorithm.equals("GA")) {
                // plan = new GA(tsp).solve();
                // TODO: 实现 GA 算法
            } else {
                System.out.println("Invalid algorithm name!");
            }

            if (plan != null) {
                System.out.printf("solved successfully, using %.2f s\n", plan.getCPUtime());
                writer.write(String.format(
                        "%-10s %-10d %-10.2f %-10.2f \n",
                        fileName.substring(0, fileName.indexOf('.')),
                        plan.getCost(), plan.getCPUtime(), plan.getMemoryUsage()));

            }
        }

        // 关闭文件写入流
        writer.close();
    }
}