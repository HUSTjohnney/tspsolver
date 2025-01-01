package com;

public class TspPlan {

    private final int[] plan; // 行走路线
    private final int cost; // 路径长度
    private final double CPUtime; // 算法运行时间

    public TspPlan(int[] plan, int cost, double CPUtime) {
        this.plan = plan;
        this.cost = cost;
        this.CPUtime = CPUtime;
    }

    // getter and setter
    public int[] getPlan() {
        return plan;
    }

    public int getCost() {
        return cost;
    }

    public double getCPUtime() {
        return CPUtime;
    }
}
