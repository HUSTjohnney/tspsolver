package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {

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
		int[][] dist = new int[numCities][numCities];
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

	public static void main(String[] args) throws IOException {
		TspProblem problem = FileReader.read("resources/eil23.txt", 23);
		int[] x = problem.getxCoors();
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}
}
