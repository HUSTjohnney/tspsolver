package com.greedy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;

/**
 * 贪心算法求解TSP问题。
 * 从0节点开始，每次选择距离最近的节点，直到所有节点都被访问过。
 */
public class Greedy {

	/**
	 * TSP问题类，包含城市数量、距离矩阵等信息
	 */
	private TspProblem problem;

	/**
	 * 被访问过的节点
	 */
	private Set<Integer> visited;

	public Greedy(TspProblem p) {
		this.problem = p;
		this.visited = new HashSet<>();
	}

	/**
	 * 对TSP问题进行贪心算法求解
	 * 
	 * @return TSP问题的解
	 */
	public TspPlan solve() {

		long startTime = System.currentTimeMillis();

		/**
		 * 初始节点，从0节点开始
		 */
		int nextCity = 0;

		/**
		 * 返回找到的路径
		 */
		int[] path = new int[problem.getCityNum()];
		path[0] = nextCity;

		visited.add(nextCity);

		while (visited.size() < problem.getCityNum()) {
			/**
			 * 从当前节点到下一个节点的距离向量
			 */
			int[] cityCost = new int[problem.getCityNum()];

			for (int i = 0; i < problem.getCityNum(); i++) {
				// 如果节点没有被访问过，设置距离为NextCity到其他节点之间的距离
				if (!visited.contains(i)) {
					cityCost[i] = problem.getDist()[nextCity][i];
				} else {
					// 被访问过的节点距离设置为无穷大的距离
					cityCost[i] = Integer.MAX_VALUE;
				}
			}

			// 打印当前节点到其他节点的距离
			System.out.print("CityCost: ");
			for (int i = 0; i < problem.getCityNum(); i++) {
				System.out.print(((cityCost[i] == Integer.MAX_VALUE) ? "MAX" : cityCost[i]) + " ");
			}
			System.out.println();

			// 基于贪心策略，找到下一个节点
			int minCost = Integer.MAX_VALUE;
			for (int i = 0; i < problem.getCityNum(); i++) {
				if (cityCost[i] < minCost) {
					minCost = cityCost[i];
					nextCity = i;
				}
			}

			// 打印下一个节点
			System.out.println("NextCity: " + nextCity + " Cost: " + minCost);

			path[visited.size()] = nextCity;

			visited.add(nextCity); // 将下一个节点加入已访问节点集合
		}

		long endTime = System.currentTimeMillis();

		return new TspPlan(path, TSPUtils.cost(path, problem.getDist()), (endTime - startTime) / 1000.00);
	}

	public static void main(String[] args) throws IOException {
		TspProblem problem = TSPUtils.read("src/main/resources/25Nodes/p01.txt", 25);
		TspPlan p = new Greedy(problem).solve();

		System.out.println("Greedy: " + p);

		System.out.println("Greedy: " + getParam());

		System.out.println("Path length:" + p.getRoute().length);
	}

	public static String getParam() {
		return "NONE PARAMETER NEEDED";
	}
}