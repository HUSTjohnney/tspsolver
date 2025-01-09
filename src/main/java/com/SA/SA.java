package com.sa;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.TspSolver;
import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;

public class SA implements TspSolver {

	private TspProblem problem;

	private static double initialTemporature = 1e6; // 初始温度
	private static double decresRate = 0.99; // 降温系数
	private static double temperatureLB = 1e-6; // 最低温度
	private static int iterTimes = 1000; // 每个温度下的迭代次数

	/**
	 * 构造函数
	 * 
	 * @param problem TSP问题
	 */
	public SA(TspProblem problem) {
		this.problem = problem;
	}

	/**
	 * 广度优先搜索，输出贪心出来的最优路径，即每次都从起始点找附近最进的点去贪心遍历，获取一个初始可行解。
	 * 
	 * @return 输出得出的遍历节点顺序
	 */
	public int[] getInitRoute() {

		int[] vis = new int[problem.getCityNum()];
		int[] ret = new int[problem.getCityNum()];
		Queue<Integer> q = new LinkedList<>();
		q.add(0);
		vis[0] = 1;
		int index = 1;
		while (!q.isEmpty()) {
			int front = q.poll();
			int min = Integer.MAX_VALUE;
			int sIdx = 0;
			for (int i = 0; i < problem.getCityNum(); i++) {
				if (vis[i] == 0 && i != front && min > problem.getDist()[front][i]) {
					min = problem.getDist()[front][i];
					sIdx = i;
				}
			}
			if (min != Integer.MAX_VALUE) {
				vis[sIdx] = 1;
				q.add(sIdx);
				ret[index] = sIdx;
				index++;
			}
		}
		q = null;
		return ret;
	}

	/**
	 * 模拟退火算法
	 * 
	 * @return 最优路径、路径长度、算法运行时间
	 */
	@Override
	public TspPlan solve() {
		long startTime = System.currentTimeMillis();
		int[] initRout = getInitRoute();
		int[] bestpath, curentpath;
		double t = initialTemporature;
		bestpath = curentpath = Arrays.copyOf(initRout, initRout.length);
		Random random = new Random();

		while (t > temperatureLB) {
			int it = 0;
			while (it < iterTimes) {
				int[] update_path = TSPUtils.swap(curentpath);
				int delta = TSPUtils.cost(update_path, problem.getDist())
						- TSPUtils.cost(curentpath, problem.getDist());
				if (delta < 0) { // 如果新路径更短，以一定概率接受新路径
					curentpath = update_path;
					bestpath = update_path;
				} else {
					double p = Math.exp(-delta / t);
					if (random.nextDouble() <= p) {
						curentpath = update_path;
					}
				}
				it++;
			}
			// 降温
			t *= decresRate;
		}

		long endTime = System.currentTimeMillis();
		double duration = (endTime - startTime) / 1000.0;
		return new TspPlan(bestpath, TSPUtils.cost(bestpath, problem.getDist()), duration);
	}

	public static void main(String[] args) throws IOException {
		TspProblem problem = TspProblem.read("src\\main\\resources\\eil51.txt", 51);
		SA sa = new SA(problem);
		int[] rout = sa.getInitRoute();
		SA.setInitialTemporature(1e6);
		SA.setDecresRate(0.99);
		SA.setTemperatureLB(1e-6);
		SA.setIterTimes(100 * rout.length);
		TspPlan plan = sa.solve();
		System.out.println(plan);
		System.out.println(plan.getRoute().length);
	}

	// getter and setter
	public TspProblem getProblem() {
		return problem;
	}

	public void setProblem(TspProblem problem) {
		this.problem = problem;
	}

	public static double getInitialTemporature() {
		return initialTemporature;
	}

	public static void setInitialTemporature(double t0) {
		initialTemporature = t0;
	}

	public static double getDecresRate() {
		return decresRate;
	}

	public static void setDecresRate(double d) {
		SA.decresRate = d;
	}

	public static double getTemperatureLB() {
		return temperatureLB;
	}

	public static void setTemperatureLB(double tk) {
		temperatureLB = tk;
	}

	public static int getIterTimes() {
		return iterTimes;
	}

	public static void setIterTimes(int l) {
		iterTimes = l;
	}

}
