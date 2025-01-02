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
	private static double T0 = 1e6; // 初始温度
	private static double d = 0.99; // 降温系数
	private static double Tk = 1e-6; // 最低温度
	private static int L = 1000; // 每个温度下的迭代次数

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
		double t = T0;
		bestpath = curentpath = Arrays.copyOf(initRout, initRout.length);
		Random random = new Random();
		//
		while (t > Tk) {
			int it = 0;
			while (it < L) {
				int[] update_path = swap(curentpath);
				int delta = cost(update_path) - cost(curentpath);
				if (delta < 0) {
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
			t *= d;
		}

		long endTime = System.currentTimeMillis();
		double duration = (endTime - startTime) / 1000.0;
		return new TspPlan(bestpath, cost(bestpath), duration);
	}

	public static void main(String[] args) throws IOException {
		TspProblem problem = TspProblem.read("src\\main\\resources\\eil51.txt", 51);
		SA sa = new SA(problem);
		int[] rout = sa.getInitRoute();
		SA.setT0(1e6);
		SA.setD(0.99);
		SA.setTk(1e-6);
		SA.setL(50 * rout.length);
		TspPlan plan = sa.solve();
		System.out.println(plan);
	}

	// getter and setter
	public TspProblem getProblem() {
		return problem;
	}

	public void setProblem(TspProblem problem) {
		this.problem = problem;
	}

	public static double getT0() {
		return T0;
	}

	public static void setT0(double t0) {
		T0 = t0;
	}

	public static double getD() {
		return d;
	}

	public static void setD(double d) {
		SA.d = d;
	}

	public static double getTk() {
		return Tk;
	}

	public static void setTk(double tk) {
		Tk = tk;
	}

	public static int getL() {
		return L;
	}

	public static void setL(int l) {
		L = l;
	}

}
