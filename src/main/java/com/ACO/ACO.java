package com.aco;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.TspProblem;

public class ACO {

	private Ant[] ants; // 蚂蚁
	private int antNum; // 蚂蚁数量
	private int cityNum; // 城市数量
	private int MAX_GEN; // 运行代数
	private float[][] pheromone; // 信息素矩阵
	private int[][] distance; // 距离矩阵
	private int bestLength; // 最佳长度
	private int[] bestTour; // 最佳路径

	// 三个参数
	private float alpha;
	private float beta;
	private float rho;

	public ACO() {

	}

	/**
	 * constructor of ACO
	 * 
	 * @param n
	 *          城市数量
	 * @param m
	 *          蚂蚁数量
	 * @param g
	 *          运行代数
	 * @param a
	 *          alpha
	 * @param b
	 *          beta
	 * @param r
	 *          rho
	 * 
	 **/
	public ACO(int n, int m, int g, float a, float b, float r) {
		cityNum = n;
		antNum = m;
		ants = new Ant[antNum];
		MAX_GEN = g;
		alpha = a;
		beta = b;
		rho = r;
	}

	// 给编译器一条指令，告诉它对被批注的代码元素内部的某些警告保持静默
	@SuppressWarnings("resource")
	/**
	 * 初始化ACO算法类
	 * 
	 * @param filename 数据文件名，该文件存储所有城市节点坐标数据
	 * @throws IOException
	 */
	private void init(String filename) throws IOException {
		// 读取数据
		TspProblem problem = TspProblem.read(filename, cityNum);
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

	public void solve() {
		// 迭代MAX_GEN次
		for (int g = 0; g < MAX_GEN; g++) {
			// antNum只蚂蚁
			for (int i = 0; i < antNum; i++) {
				// i这只蚂蚁走cityNum步，完整一个TSP
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

		// 打印最佳结果
		printOptimal();
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

	private void printOptimal() {
		System.out.println("The optimal length is: " + bestLength);
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < cityNum + 1; i++) {
			System.out.print(bestTour[i]+"->");
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Start....");
		ACO aco = new ACO(48, 10, 100, 1.f, 5.f, 0.5f);
		aco.init("src\\main\\resources\\eil51.txt");
		aco.solve();
	}

}