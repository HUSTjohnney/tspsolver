package com.aco;

import java.io.IOException;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;
import com.TspSolver;

public class ACO implements TspSolver {

	private final TspProblem problem;

	private Ant[] ants; // 蚂蚁

	private int cityNum; // 城市数量

	private float[][] pheromone; // 信息素矩阵
	private int bestCost; // 最佳长度
	private int[] bestRoute; // 最佳路径

	/**
	 * ACO算法所需的参数
	 */
	private static int antNum; // 蚂蚁数量
	private static int MAX_GEN; // 迭代次数
	private static float ALPHA; // 信息启发因子
	private static float BETA; // 期望启发因子
	private static float RHO; // 信息素挥发因子

	/**
	 * ACO算法的构造函数
	 * 
	 * @param problem TSP问题
	 * @param antNum  蚂蚁数量
	 * @param max_gen 最大迭代次数
	 */
	public ACO(TspProblem problem) {
		this.problem = problem;
		this.cityNum = problem.getCityNum();
		this.ants = new Ant[antNum];
		init();
	}

	/**
	 * 初始化ACO算法类
	 * 
	 */
	private void init() {

		// 初始化信息素矩阵
		pheromone = new float[cityNum][cityNum];
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				pheromone[i][j] = 0.1f; // 初始化为0.1
			}
		}
		bestCost = Integer.MAX_VALUE;
		bestRoute = new int[cityNum + 1];
		// 随机放置蚂蚁
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(cityNum, ALPHA, BETA);
			ants[i].init(problem.getDist());
		}
	}

	public TspPlan solve() {
		long startTime = System.currentTimeMillis();
		// 迭代MAX_GEN次
		for (int g = 0; g < MAX_GEN; g++) {
			// antNum只蚂蚁
			for (int i = 0; i < antNum; i++) {
				// 这只蚂蚁走cityNum步，完整一个TSP
				for (int j = 1; j < cityNum; j++) {
					ants[i].selectNextCity(pheromone);
				}
				// 把这只蚂蚁起始城市加入其禁忌表中
				// 禁忌表最终形式：起始城市,城市1,城市2...城市n,起始城市
				ants[i].getTabu().add(ants[i].getFirstCity());
				// 查看这只蚂蚁行走路径距离是否比当前距离优秀
				if (ants[i].getRouteLength() < bestCost) {
					// 比当前优秀则拷贝优秀TSP路径
					bestCost = ants[i].getRouteLength();
					for (int k = 0; k < cityNum + 1; k++) {
						bestRoute[k] = ants[i].getTabu().get(k).intValue();
					}
				}
				// 更新这只蚂蚁的信息数变化矩阵，对称矩阵
				for (int j = 0; j < cityNum; j++) {
					ants[i].getDelta()[ants[i].getTabu().get(j).intValue()][ants[i]
							.getTabu().get(j + 1).intValue()] = (float) (1. / ants[i]
									.getRouteLength());
					ants[i].getDelta()[ants[i].getTabu().get(j + 1).intValue()][ants[i]
							.getTabu().get(j).intValue()] = (float) (1. / ants[i]
									.getRouteLength());
				}
			}
			// 更新信息素
			updatePheromone();
			// 重新初始化蚂蚁
			for (int i = 0; i < antNum; i++) {
				ants[i].init(problem.getDist());
			}
		}

		long endTime = System.currentTimeMillis();
		return new TspPlan(bestRoute, bestCost, (endTime - startTime) / 1000.0);
	}

	// 更新信息素
	private void updatePheromone() {
		// 信息素挥发
		for (int i = 0; i < cityNum; i++)
			for (int j = 0; j < cityNum; j++)
				pheromone[i][j] = pheromone[i][j] * (1 - RHO);

		// 信息素更新
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				for (int k = 0; k < antNum; k++) {
					pheromone[i][j] += ants[k].getDelta()[i][j];
				}
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		TspProblem problem = TSPUtils.read("src\\main\\resources\\eil51.txt", 51);
		ACO aco = new ACO(problem);
		ACO.setAntNum(20);
		ACO.setMAX_GEN(100);
		ACO.setALPHA(1.f);
		ACO.setBETA(5.f);
		ACO.setRHO(0.5f);
		TspPlan plan = aco.solve();
		System.out.println("The plan is: " + plan);
	}

	// getter and setter
	public static float getALPHA() {
		return ALPHA;
	}

	public static void setALPHA(float alpha) {
		ACO.ALPHA = alpha;
	}

	public static float getBETA() {
		return BETA;
	}

	public static void setBETA(float beta) {
		ACO.BETA = beta;
	}

	public static float getRHO() {
		return RHO;
	}

	public static void setRHO(float rho) {
		ACO.RHO = rho;
	}

	public static void setAntNum(int antNum) {
		ACO.antNum = antNum;
	}

	public static void setMAX_GEN(int mAX_GEN) {
		MAX_GEN = mAX_GEN;
	}

	public static String getParam() {
		return "ALPHA=" + ALPHA + ", BETA=" + BETA + ", RHO=" + RHO;
	}

}