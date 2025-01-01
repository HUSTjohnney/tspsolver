package com.SA;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.FileReader;
import com.TspProblem;

public class SA {

	TspProblem problem;

	public SA(TspProblem problem) {
		this.problem = problem;
	}

	/**
	 * 初始解获取
	 * 
	 * @return 初始解
	 */
	public int[] BFS() {
		Queue<Integer> q = new LinkedList<>();
		q.add(0);
		int[] vis = new int[problem.getxCoors().length];
		int[] out = new int[problem.getxCoors().length];
		vis[0] = 1;
		int totalDist = 0;
		int index = 1;
		while (!q.isEmpty()) {
			int front = q.poll();
			int min = Integer.MAX_VALUE;
			int sIdx = 0;
			for (int i = 0; i < problem.getxCoors().length; i++) {
				if (vis[i] == 0 && i != front && min > problem.getDistance()[front][i]) {
					min = problem.getDistance()[front][i];
					sIdx = i;
				}
			}
			if (min != Integer.MAX_VALUE) {
				vis[sIdx] = 1;
				q.add(sIdx);
				out[index] = sIdx;
				index++;
				totalDist += problem.getDistance()[front][sIdx];
			}
		}
		q = null;
		totalDist += problem.getDistance()[out[out.length - 1]][0];
		return out;
	}

	public int cost(int[] rout) {
		int sum = 0;
		int[][] dist = problem.getDistance();
		for (int i = 0; i < rout.length - 1; i++) {
			sum += dist[rout[i]][rout[i + 1]];
		}
		sum += dist[rout[rout.length - 1]][rout[0]];
		return sum;
	}

	public int[] copyRout(int[] rout) {
		int[] out = new int[rout.length];
		for (int i = 0; i < rout.length; i++)
			out[i] = rout[i];
		return out;
	}

	/**
	 * 交换两个城市的位置
	 * 
	 * @param rout 初始路径
	 * @return 交换后的路径
	 */
	public int[] swap(int[] rout) {
		Random random = new Random();
		int r1 = random.nextInt(rout.length);
		int r2 = random.nextInt(rout.length);
		while (r1 == r2) {
			r2 = random.nextInt(rout.length);
		}
		int[] change = copyRout(rout);
		int tmp = change[r1];
		change[r1] = change[r2];
		change[r2] = tmp;
		return change;
	}

	/**
	 * ģ���˻��㷨SA
	 * 
	 * @param rout �������ڵ�����·��
	 * @param T0   ��ʼ�¶�
	 * @param d    �¶�˥���ʣ�0.98
	 * @param Tk   ����¶�
	 * @param L    ��ѭ������
	 * @return ����õ�������·��
	 */
	public int[] solve(int[] rout, double T0, double d, double Tk, int L) {
		// T0=1e5,d =1-7e-3, Tk=1e-3
		int[] solution = new int[rout.length];
		int[] bestpath, curentpath;
		double t = T0;
		bestpath = curentpath = copyRout(rout);
		Random random = new Random();
		while (t > Tk) {
			int it = 0;
			while (it < L) {
				int[] update_path = swap(curentpath);
				int delta = cost(update_path) - cost(curentpath);
				if (delta < 0) {// Ϊ��ֵ��������ɱ������ˣ������
					curentpath = update_path;
					bestpath = update_path;
				} else {
					double p = Math.exp(-delta / t);
					if (random.nextDouble() <= p) {
						curentpath = update_path;
					}
				}
				it++;
			}
			t *= d;
		}
		return bestpath;
	}

	public void print(int rout[]) {
		System.out.println("Cost:" + cost(rout));
		System.out.print("Rout:" + rout[0]);
		for (int i = 1; i < rout.length; i++) {
			System.out.print("->" + rout[i]);
		}
		System.out.println("->0");
	}

	public static void main(String[] args) throws IOException {
		TspProblem problem = FileReader.read("src\\main\\resources\\eil51.txt", 51);
		SA sa = new SA(problem);
		int[] rout = sa.BFS();
		double T0 = 1e6;
		double d = 0.99;
		double Tk = 1e-6;
		int L = 20 * rout.length;// ��ѭ������
		int[] rout2 = sa.solve(rout, T0, d, Tk, L);
		sa.print(rout2);
	}

}
