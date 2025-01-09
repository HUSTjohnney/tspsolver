package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TspProblem {
	private final int[] xCoors; // 城市的x坐标
	private final int[] yCoors; // 城市的y坐标
	private final int[][] dist; // 两个城市之间的距离\
	private final int cityNum; // 城市数量

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
		this.cityNum = xCoors.length;
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
		this.cityNum = problem.getCityNum();
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

	/**
	 * 计算两个城市之间的目标函数距离
	 * 
	 * @param dist  距离矩阵
	 * @param route 可行路径
	 * @return 路径长度
	 */
	public int calculateDistance(int[] route) {
		int distance = 0;
		for (int i = 0; i < route.length - 1; i++) {
			distance += dist[route[i]][route[i + 1]];
		}
		distance += dist[route[route.length - 1]][route[0]]; // 回到起点
		return distance;
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

	public int getCityNum() {
		return cityNum;
	}

}
