package com.sa;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.TspSolver;
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

		int[] vis = new int[problem.getxCoors().length];
		int[] ret = new int[problem.getxCoors().length];
		Queue<Integer> q = new LinkedList<>();
		q.add(0);
		vis[0] = 1;
		int index = 1;
		while (!q.isEmpty()) {
			int front = q.poll();
			int min = Integer.MAX_VALUE;
			int sIdx = 0;
			for (int i = 0; i < problem.getxCoors().length; i++) {
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
	 * 计算路径长度
	 * 
	 * @param rout 路径
	 * @return 路径长度
	 */
	public int cost(int[] rout) {
		int sum = 0;
		int[][] dist = problem.getDist();
		for (int i = 0; i < rout.length - 1; i++) {
			sum += dist[rout[i]][rout[i + 1]];
		}
		sum += dist[rout[rout.length - 1]][rout[0]];
		return sum;
	}

	/**
	 * 交换两个城市的位置
	 * 
	 * @param rout 初始路径
	 * @return 交换后的路径
	 */
	public int[] swap(int[] rout) {
		Random random = new Random();
		int r1 = random.nextInt(rout.length);
		int r2 = random.nextInt(rout.length);
		while (r1 == r2) {
			r2 = random.nextInt(rout.length);
		}
		int[] change = Arrays.copyOf(rout, rout.length);
		int tmp = change[r1];
		change[r1] = change[r2];
		change[r2] = tmp;
		return change;
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
				int[] update_path = swap(curentpath);
				int delta = cost(update_path) - cost(curentpath);
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
		return new TspPlan(bestpath, cost(bestpath), duration);
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
