package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TspProblem {
	private final int[] xCoors; // 城市的x坐标
	private final int[] yCoors; // 城市的y坐标
	private final int[][] dist; // 两个城市之间的距离

	/**
	 * 构造函数，初始化城市坐标和城市之间的距离
	 * 
	 * @param xCoors 城市的x坐标
	 * @param yCoors 城市的y坐标
	 */
	public TspProblem(int[] xCoors, int[] yCoors) {
		super();
		this.xCoors = xCoors;
		this.yCoors = yCoors;
		this.dist = calculateDists();
	}

	/**
	 * 基于已有的TspProblem对象构造新的TspProblem对象
	 * 
	 * @param problem 已有的TspProblem对象
	 */
	public TspProblem(TspProblem problem) {
		this.xCoors = problem.getxCoors();
		this.yCoors = problem.getyCoors();
		this.dist = problem.getDist();
	}

	/**
	 * 计算城市之间的距离
	 * 
	 * @return 两个城市之间的距离
	 */
	private int[][] calculateDists() {
		int[][] ret = new int[xCoors.length][xCoors.length];
		for (int i = 0; i < xCoors.length - 1; i++) {
			ret[i][i] = 0;
			for (int j = i + 1; j < xCoors.length; j++) {
				ret[i][j] = EUC_2D_dist(xCoors[i], xCoors[j], yCoors[i], yCoors[j]);
				ret[j][i] = ret[i][j];
			}
		}
		return ret;
	}

	/**
	 * 计算两个城市之间的欧式距离
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @return
	 */
	public static int EUC_2D_dist(int x1, int x2, int y1, int y2) {
		return (int) Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
	}

	// 计算 TSP 的路径长度
	public static int calculateDistance(int[][] dist, int[] solution) {
		int distance = 0;
		for (int i = 0; i < solution.length - 1; i++) {
			distance += dist[solution[i]][solution[i + 1]];
		}
		distance += dist[solution[solution.length - 1]][solution[0]]; // 回到起点
		return distance;
	}

	/**
	 * 读取TSP问题
	 * 
	 * @param filename
	 * @param numCities
	 * @return
	 * @throws IOException
	 */
	public static TspProblem read(String filename, int numCities) throws IOException {
		int[] x;
		int[] y;
		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));
		x = new int[numCities];
		y = new int[numCities];

		while ((strbuff = data.readLine()) != null) {
			if (!Character.isAlphabetic(strbuff.charAt(0)))
				break;
		}
		String[] tmp = strbuff.split(" ");
		x[0] = Integer.valueOf(tmp[1]);
		y[0] = Integer.valueOf(tmp[2]);

		for (int i = 1; i < numCities; i++) {
			strbuff = data.readLine();
			String[] strcol = strbuff.split(" ");
			x[i] = Integer.valueOf(strcol[1]);
			y[i] = Integer.valueOf(strcol[2]);
		}
		data.close();

		TspProblem problem = new TspProblem(x, y);

		return problem;
	}

	// getters and setters
	public int[] getxCoors() {
		return xCoors;
	}

	public int[] getyCoors() {
		return yCoors;
	}

	public int[][] getDist() {
		return dist;
	}

}
