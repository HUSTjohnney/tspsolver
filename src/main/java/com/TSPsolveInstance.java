package com;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.Cplex.CplexSolver;
import com.aco.ACO;
import com.alns.ALNS;
import com.ga.GA;
import com.greedy.Greedy;
import com.sa.SA;
import com.tabu.TS;

public class TSPsolveInstance {
    public static void main(String[] args) throws IOException {

        // 算例的节点数量
        int nodeNum = 100;

        // 选择算法："CPLEX"/"SA"/"Greedy"/"GA"/"ACO/TS/ALNS"
        String algorithm = "TS";
        String para = "";

        if (algorithm.equals("CPLEX")) {
            para = CplexSolver.getParam();
        } else if (algorithm.equals("SA")) {
            SA.setINIT_TEMP(1e5 * nodeNum);
            SA.setDECRESE_RATE(0.995);
            SA.setTEMP_LB(1e-7);
            SA.setMAX_ITER_TIME(50 * nodeNum);
            para = SA.getParam();
        } else if (algorithm.equals("Greedy")) {
            para = Greedy.getParam();
        } else if (algorithm.equals("GA")) {
            GA.setCHORO_NUM(nodeNum * 30); // 种群数量
            GA.setCROSS_RATE(0.9); // 交叉率
            GA.setMUTATE_RATE(0.1); // 变异率
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
        } else if (algorithm.equals("TS")) {
            TS.setMAX_ITER(1000);
            TS.setTABU_TENURE(500);
            TS.setINIT_ROUTENUM(10);
            para = TS.getParam();
        } else if (algorithm.equals("ALNS")) {
            para = "ALNS";
        } else {
            System.out.println("Invalid algorithm name!");
            return;
        }

        String filePath = "src\\main\\resources\\tsp\\" + nodeNum + "Nodes\\";
        // 创建 FileWriter 对象，用于写入结果到文件
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(getAvailableFileName("src/main/resources/tsp/results/", algorithm, nodeNum)));

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
            TspProblem tsp = TSPUtils.read(filePath + fileName); // 解析 STND
            System.out.println("Solving " + fileName + "... by " + algorithm + " algorithm");

            TspPlan plan = null;

            if (algorithm.equals("CPLEX")) {
                plan = new CplexSolver(tsp).solve();
            } else if (algorithm.equals("SA")) {
                plan = new SA(tsp).solve();
            } else if (algorithm.equals("Greedy")) {
                plan = new Greedy(tsp).solve();
            } else if (algorithm.equals("GA")) {
                plan = new GA(tsp).solve();
            } else if (algorithm.equals("ACO")) {
                plan = new ACO(tsp).solve();
            } else if (algorithm.equals("TS")) {
                plan = new TS(tsp).solve();
            } else if (algorithm.equals("ALNS")) {
                plan = new ALNS(tsp).solve();
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

    /**
     * 获取可用的文件名
     * 
     * @param directory 文件目录
     * @param algorithm 算法名称
     * @param nodeNum   节点数量
     * @return 可用的文件名
     */
    private static String getAvailableFileName(String directory, String algorithm, int nodeNum) {
        String baseName = directory + algorithm + "_" + nodeNum;
        String fileName = baseName + ".txt";
        File file = new File(fileName);
        int counter = 1;
        // 检查文件是否存在，如果存在则添加编号后缀
        while (file.exists()) {
            fileName = baseName + "(" + counter + ")" + ".txt";
            file = new File(fileName);
            counter++;
        }
        return fileName;
    }
}