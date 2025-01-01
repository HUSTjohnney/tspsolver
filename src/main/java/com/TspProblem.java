package com;

public class TspProblem {
	private int[] xCoors; // 城市的x坐标
	private int[] yCoors; // 城市的y坐标
	private int[][] distance; // 两个城市之间的距离

	private double maxDist; // 两个城市之间的最大距离

	public TspProblem(int[] xCoors, int[] yCoors) {
		super();
		this.xCoors = xCoors;
		this.yCoors = yCoors;
		buildDistsArrays();
	}

	private void buildDistsArrays() {
		maxDist = -1;
		distance = new int[xCoors.length][xCoors.length];
		for (int i = 0; i < xCoors.length - 1; i++) {
			distance[i][i] = 0;
			for (int j = i + 1; j < xCoors.length; j++) {
				distance[i][j] = EUC_2D_dist(xCoors[i], xCoors[j], yCoors[i], yCoors[j]);
				distance[j][i] = distance[i][j];
				if (distance[i][j] > maxDist) {
					maxDist = distance[i][j];
				}
			}
		}
	}

	public int[] getxCoors() {
		return xCoors;
	}

	public int[] getyCoors() {
		return yCoors;
	}

	public int[][] getDistance() {
		return distance;
	}

	public double getMaxDist() {
		return maxDist;
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

}
