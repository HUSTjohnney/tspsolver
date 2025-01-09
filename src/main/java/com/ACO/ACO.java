package com.aco;

import java.io.IOException;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;
import com.TspSolver;

public class ACO implements TspSolver {

	private final TspProblem problem;

	private Ant[] ants; // 蚂蚁
	private int antNum; // 蚂蚁数量
	private int cityNum; // 城市数量
	private int MAX_GEN; // 运行代数
	private float[][] pheromone; // 信息素矩阵
	private int[][] distance; // 距离矩阵
	private int bestLength; // 最佳长度
	private int[] bestTour; // 最佳路径

	private static float alpha; // 信息启发因子
	private static float beta; // 期望启发因子
	private static float rho; // 信息素挥发因子

	/**
	 * ACO算法的构造函数
	 * 
	 * @param problem TSP问题
	 * @param antNum  蚂蚁数量
	 * @param max_gen 最大迭代次数
	 */
	public ACO(TspProblem problem, int antNum, int max_gen) {
		this.problem = problem;
		this.cityNum = problem.getCityNum();
		this.antNum = antNum;
		this.ants = new Ant[antNum];
		this.MAX_GEN = max_gen;
		init();
	}

	/**
	 * 初始化ACO算法类
	 * 
	 */
	private void init() {
		// 读取数据
		distance = problem.getDist();

		// 初始化信息素矩阵
		pheromone = new float[cityNum][cityNum];
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				pheromone[i][j] = 0.1f; // 初始化为0.1
			}
		}
		bestLength = Integer.MAX_VALUE;
		bestTour = new int[cityNum + 1];
		// 随机放置蚂蚁
		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant(cityNum);
			ants[i].init(distance, alpha, beta);
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
				if (ants[i].getTourLength() < bestLength) {
					// 比当前优秀则拷贝优秀TSP路径
					bestLength = ants[i].getTourLength();
					for (int k = 0; k < cityNum + 1; k++) {
						bestTour[k] = ants[i].getTabu().get(k).intValue();
					}
				}
				// 更新这只蚂蚁的信息数变化矩阵，对称矩阵
				for (int j = 0; j < cityNum; j++) {
					ants[i].getDelta()[ants[i].getTabu().get(j).intValue()][ants[i]
							.getTabu().get(j + 1).intValue()] = (float) (1. / ants[i]
									.getTourLength());
					ants[i].getDelta()[ants[i].getTabu().get(j + 1).intValue()][ants[i]
							.getTabu().get(j).intValue()] = (float) (1. / ants[i]
									.getTourLength());
				}
			}
			// 更新信息素
			updatePheromone();
			// 重新初始化蚂蚁
			for (int i = 0; i < antNum; i++) {
				ants[i].init(distance, alpha, beta);
			}
		}

		long endTime = System.currentTimeMillis();
		return new TspPlan(bestTour, bestLength, (endTime - startTime) / 1000.0);
	}

	// 更新信息素
	private void updatePheromone() {
		// 信息素挥发
		for (int i = 0; i < cityNum; i++)
			for (int j = 0; j < cityNum; j++)
				pheromone[i][j] = pheromone[i][j] * (1 - rho);

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
		ACO aco = new ACO(problem, 10, 100);
		ACO.setAlpha(1.f);
		ACO.setBeta(5.f);
		ACO.setRho(0.5f);
		TspPlan plan = aco.solve();
		System.out.println("The plan is: " + plan);
	}

	// getter and setter
	public static float getAlpha() {
		return alpha;
	}

	public static void setAlpha(float alpha) {
		ACO.alpha = alpha;
	}

	public static float getBeta() {
		return beta;
	}

	public static void setBeta(float beta) {
		ACO.beta = beta;
	}

	public static float getRho() {
		return rho;
	}

	public static void setRho(float rho) {
		ACO.rho = rho;
	}

}