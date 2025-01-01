package com;

public class TspProblem {
	private final int[] xCoors; // 城市的x坐标
	private final int[] yCoors; // 城市的y坐标
	private final int[][] dist; // 两个城市之间的距离

	public TspProblem(int[] xCoors, int[] yCoors) {
		super();
		this.xCoors = xCoors;
		this.yCoors = yCoors;
		this.dist = calculateDists();
	}

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
