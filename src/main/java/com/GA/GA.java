package com.ga;

import java.io.IOException;
import java.util.Arrays;

import com.TspProblem;
import com.TspSolver;
import com.TSPUtils;
import com.TspPlan;

public class GA implements TspSolver {

	/**
	 * TSP问题对象，记录城市数量、城市坐标、城市之间的距离
	 */
	private final TspProblem problem;

	/**
	 * 染色体数组
	 */
	private int[][] chromosomes;

	/**
	 * 种群大小
	 */
	private static int CHORO_NUM = 200;

	/**
	 * 交叉概率，即两个染色体进行交叉的概率
	 */
	private static double CROSS_RATE = 0.9;

	/**
	 * 变异概率，即染色体进行变异的概率
	 */
	private static double MUTATE_RATE = 0.1;

	/**
	 * 最大遗传代数
	 */
	private static int MAX_GEN = 500;

	/**
	 * 精英保留策略的比例
	 */
	private static double ELITE_RATE = 0.2;

	/**
	 * 基于 TSP 问题构造 GA 类
	 * 
	 * @param problem TSP 问题
	 */
	public GA(TspProblem problem) {
		this.problem = problem;
		this.chromosomes = new int[CHORO_NUM][problem.getCityNum()];

		// 初始化种群
		for (int i = 0; i < CHORO_NUM; i++) {
			chromosomes[i] = TSPUtils.findRandomRoute(problem.getCityNum());
		}
	}

	/**
	 * 选择操作，使用轮盘赌选择法，基本思想是按照染色体的适应度来分配选择的概率，适应度越高的染色体被选中的概率越大。
	 * 
	 */
	private int[][] selection() {
		int[][] selectedChromosomes = new int[CHORO_NUM][problem.getCityNum()];
		double[] fitness = new double[CHORO_NUM];
		double totalFitness = 0;

		// 计算每个染色体的适应度、以及总适应度。
		for (int i = 0; i < CHORO_NUM; i++) {
			// 适应度为路径长度的倒数
			fitness[i] = 1.0 / TSPUtils.cost(chromosomes[i], problem.getDist());
			totalFitness += fitness[i];
		}

		// 精英保留策略：先保留最优的几个染色体
		int eliteSize = (int) (CHORO_NUM * ELITE_RATE);
		double eliteFitnessSum = 0;
		int[][] eliteChromosomes = getEliteChromosomes(eliteSize);
		for (int i = 0; i < eliteSize; i++) {
			selectedChromosomes[i] = eliteChromosomes[i];
			eliteFitnessSum += fitness[i];
		}

		// 轮盘赌选择剩下的染色体
		for (int i = eliteSize; i < CHORO_NUM; i++) {
			double rouletteValue = Math.random() * (totalFitness - eliteFitnessSum);
			double cumulativeFitness = 0;
			for (int j = 0; j < CHORO_NUM; j++) {
				cumulativeFitness += fitness[j];
				if (cumulativeFitness >= rouletteValue) {
					selectedChromosomes[i] = chromosomes[j];
					break;
				}
			}
		}

		return selectedChromosomes;
	}

	private int[][] getEliteChromosomes(int eliteSize) {
		int[][] eliteChromosomes = new int[eliteSize][problem.getCityNum()];
		double[] eliteFitness = new double[eliteSize];
		Arrays.fill(eliteFitness, Double.MAX_VALUE);
		for (int i = 0; i < CHORO_NUM; i++) {
			double cost = TSPUtils.cost(chromosomes[i], problem.getDist());
			for (int j = 0; j < eliteSize; j++) {
				if (cost < eliteFitness[j]) {
					for (int k = eliteSize - 1; k > j; k--) {
						eliteFitness[k] = eliteFitness[k - 1];
						eliteChromosomes[k] = eliteChromosomes[k - 1];
					}
					eliteFitness[j] = cost;
					eliteChromosomes[j] = chromosomes[i];
					break;
				}
			}
		}
		return eliteChromosomes;
	}

	/**
	 * 交叉操作
	 * 
	 * @param selectedChromosomes 选择后的染色体数组
	 */
	private int[][] crossover(int[][] selectedChromosomes) {
		int[][] offspring = new int[CHORO_NUM][problem.getCityNum()];

		for (int i = 0; i < CHORO_NUM; i += 2) {
			if (Math.random() < CROSS_RATE) {
				int[][] children = crossover2Chromosome(selectedChromosomes[i], selectedChromosomes[i + 1]);
				offspring[i] = children[0];
				offspring[i + 1] = children[1];
			} else {
				offspring[i] = selectedChromosomes[i];
				offspring[i + 1] = selectedChromosomes[i + 1];
			}
		}

		return offspring;
	}

	/**
	 * 两点交叉操作
	 * 
	 * @param parent1 父代染色体1
	 * @param parent2 父代染色体2
	 * @return 两个子代染色体
	 */
	public static int[][] crossover2Chromosome(int[] parent1, int[] parent2) {
		int n = parent1.length;
		int[] child1 = new int[n];
		int[] child2 = new int[n];
		Arrays.fill(child1, -1);
		Arrays.fill(child2, -1);

		// 随机选择交叉的起始位置和结束位置
		int start = (int) (Math.random() * n);
		int end = (int) (Math.random() * n);
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}

		// 直接复制父代染色体的交叉区域
		for (int i = start; i <= end; i++) {
			child1[i] = parent1[i];
			child2[i] = parent2[i];
		}

		// 填充 child1 的剩余位置
		int index = 0;
		for (int i = 0; i < n; i++) {
			if (index == start) {
				index = end + 1;
			}
			if (child1[i] == -1) {
				for (int gene : parent2) {
					if (!contains(child1, gene)) {
						child1[i] = gene;
						break;
					}
				}
			}
		}

		// 填充 child2 的剩余位置
		index = 0;
		for (int i = 0; i < n; i++) {
			if (index == start) {
				index = end + 1;
			}
			if (child2[i] == -1) {
				for (int gene : parent1) {
					if (!contains(child2, gene)) {
						child2[i] = gene;
						break;
					}
				}
			}
		}

		return new int[][] { child1, child2 };
	}

	private static boolean contains(int[] array, int value) {
		for (int element : array) {
			if (element == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 变异操作
	 * 
	 * @param offspring 交叉后的子代染色体数组
	 */
	private void mutation(int[][] offspring) {
		for (int i = 0; i < CHORO_NUM; i++) {
			if (Math.random() < MUTATE_RATE) {
				offspring[i] = TSPUtils.swap(offspring[i]);
			}
		}
	}

	@Override
	public TspPlan solve() {
		long startTime = System.currentTimeMillis();
		for (int gen = 0; gen < MAX_GEN; gen++) {
			int[][] selectedChromosomes = selection();
			chromosomes = selectedChromosomes;
			// printPopulation(-2);
			int[][] offspring = crossover(selectedChromosomes);
			mutation(offspring);
			chromosomes = offspring;
			// printPopulation(gen);
		}

		/**
		 * 找到当前种群中的最优染色体
		 * 
		 * @return 最优染色体及其成本的 TspPlan 对象
		 */
		int[] bestRoute = chromosomes[0];
		int bestCost = TSPUtils.cost(bestRoute, problem.getDist());

		for (int i = 1; i < CHORO_NUM; i++) {
			int cost = TSPUtils.cost(chromosomes[i], problem.getDist());
			if (cost < bestCost) {
				bestRoute = chromosomes[i];
				bestCost = cost;
			}
		}
		long endTime = System.currentTimeMillis();
		double duration = (endTime - startTime) / 1000.0;
		return new TspPlan(bestRoute, bestCost, duration);
	}

	public static void main(String[] args) throws IOException {
		TspProblem problem = TSPUtils.read("src/main/resources/25Nodes/p01.txt", 25);
		TspPlan p = new GA(problem).solve();
		System.out.println("GA: " + p);

	}

	/**
	 * 打印一代种群，及其路径成本
	 */
	public void printPopulation(int gen) {
		System.out.println("_______________________________Generation:" + gen + "_______________________________");
		int totoaCost = 0;
		for (int i = 0; i < CHORO_NUM; i++) {
			int cost = TSPUtils.cost(chromosomes[i], problem.getDist());
			totoaCost += cost;
			System.out
					.println(Arrays.toString(chromosomes[i]) + " " + cost);
		}

		System.out.println(
				"___________________avgCost:" + totoaCost / CHORO_NUM + "___________________________________");
	}

	// gettes and setters
	public static void setCHORO_NUM(int n) {
		CHORO_NUM = n;
	}

	public static void setCROSS_RATE(double p_c_t) {
		GA.CROSS_RATE = p_c_t;
	}

	public static void setMUTATE_RATE(double p_m_t) {
		GA.MUTATE_RATE = p_m_t;
	}

	public static void setMAX_GEN(int mAX_GEN) {
		MAX_GEN = mAX_GEN;
	}

	public static void setELITE_RATE(double eliteRate) {
		GA.ELITE_RATE = eliteRate;
	}

	public static String getParam() {
		return "CHORO_NUM=" + CHORO_NUM + ", CROSS_RATE=" + CROSS_RATE + ", MUTATE_RATE=" + MUTATE_RATE
				+ ", MAX_GEN=" + MAX_GEN + ", ELITE_RATE=" + ELITE_RATE;
	}

}
