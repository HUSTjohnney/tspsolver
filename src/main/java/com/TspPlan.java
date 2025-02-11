package com;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;

public class TspPlan {

    private final int[] route; // 行走路线
    private final int cost; // 路径长度
    private final double CPUtime; // 算法运行时间

    /**
     * 构造一个TSP方案
     * 
     * @param plan    行走路线
     * @param cost    路径长度
     * @param CPUtime 算法运行时间
     */
    public TspPlan(int[] plan, int cost, double CPUtime) {
        this.route = plan;
        this.cost = cost;
        this.CPUtime = CPUtime;
    }

    // getter and setter
    public int[] getRoute() {
        return route;
    }

    public int getCost() {
        return cost;
    }

    public double getCPUtime() {
        return CPUtime;
    }

    @Override
    public String toString() {
        return "TspPlan [Route=" + print(route) + ", cost=" + cost + ", CPUtime=" + CPUtime + "]";
    }

    public static String print(int rout[]) {
        StringBuilder sb = new StringBuilder();
        sb.append(rout[0]);
        for (int i = 1; i < rout.length; i++) {
            sb.append("->").append(rout[i]);
        }
        return sb.toString();
    }

    public double getMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        long usedMemory = heapMemoryUsage.getUsed();
        return usedMemory / (1024.0 * 1024.0); // 将字节转换为 MB
    }

    public TspPlan copy() {
        return new TspPlan(Arrays.copyOf(route, route.length), cost, CPUtime);
    }

}
