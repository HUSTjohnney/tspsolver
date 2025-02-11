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

	/**
	 * TSP问题实例
	 */
	private final TspProblem problem;

	/**
	 * 初始温度
	 */
	private static double INIT_TEMP = 1e6;

	/**
	 * 降温系数
	 */
	private static double DECRESE_RATE = 0.99;

	/**
	 * 最低温度
	 */
	private static double TEMP_LB = 1e-6;

	/**
	 * 每个温度下的迭代次数
	 */
	private static int MAX_ITER_TIME = 1000;

	/**
	 * 构造函数
	 * 
	 * @param problem TSP问题
	 */
	public SA(TspProblem problem) {
		this.problem = problem;
	}

	/**
	 * 贪心算法，每次都从当前节点找附近最近距离的点作为下一个节点。
	 * 当所有节点都被访问过后，返回遍历的节点顺序。
	 * 
	 * @return 输出遍历节点顺序
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
		double t = INIT_TEMP;
		bestpath = curentpath = Arrays.copyOf(initRout, initRout.length);
		Random random = new Random();

		while (t > TEMP_LB) {
			int it = 0;
			while (it < MAX_ITER_TIME) {
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
			t *= DECRESE_RATE;
		}

		long endTime = System.currentTimeMillis();
		double duration = (endTime - startTime) / 1000.0;
		return new TspPlan(bestpath, TSPUtils.cost(bestpath, problem.getDist()), duration);
	}

	public static void main(String[] args) throws IOException {
		TspProblem problem = TSPUtils.read("src\\main\\resources\\tsp\\25Nodes\\p01.txt");
		//TspProblem problem = TSPUtils.read("src\\main\\resources\\tsp\\eil51.txt");
		SA sa = new SA(problem);
		int[] rout = sa.getInitRoute();
		SA.setINIT_TEMP(1e6);
		SA.setDECRESE_RATE(0.99);
		SA.setTEMP_LB(1e-6);
		SA.setMAX_ITER_TIME(100 * rout.length);
		TspPlan plan = sa.solve();
		System.out.println(plan);
		System.out.println(SA.getParam());
		System.out.println("Path length:" + plan.getRoute().length);
		System.out.println("Is valid solution: " + TSPUtils.isValid(plan.getRoute()));
	}

	// getter and setter
	public TspProblem getProblem() {
		return problem;
	}

	public static double getINIT_TEMP() {
		return INIT_TEMP;
	}

	public static void setINIT_TEMP(double t0) {
		INIT_TEMP = t0;
	}

	public static double getDECRESE_RATE() {
		return DECRESE_RATE;
	}

	public static void setDECRESE_RATE(double d) {
		SA.DECRESE_RATE = d;
	}

	public static double getTEMP_LB() {
		return TEMP_LB;
	}

	public static void setTEMP_LB(double tk) {
		TEMP_LB = tk;
	}

	public static int getMAX_ITER_TIME() {
		return MAX_ITER_TIME;
	}

	public static void setMAX_ITER_TIME(int l) {
		MAX_ITER_TIME = l;
	}

	public static String getParam() {
		return "INIT_TEMP=" + INIT_TEMP + ", DECRESE_RATE=" + DECRESE_RATE + ", TEMP_LB=" + TEMP_LB + ", MAX_ITER_TIME="
				+ MAX_ITER_TIME;
	}

}
