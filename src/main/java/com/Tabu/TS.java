package com.Tabu;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TS {
	/** �������� */
	private int MAX_GEN;
	/** ÿ�������ھӸ��� */
	private int neighbourhoodNum;
	/** ���ɳ��� */
	private int tabuTableLength;
	/** �ڵ����������볤�� */
	private int nodeNum;
	/** �ڵ�������� */
	private int[][] nodeDistance;
	/** ��ǰ·�� */
	private int[] route;
	/** ��õ�·�� */
	public int[] bestRoute;
	/** ���·���ܳ��� */
	private int bestEvaluation;
	/** ���ɱ� */
	private int[][] tabuTable;
	/** ���ɱ��е�����ֵ */
	private int[] tabuTableEvaluate;

	private long tp;

	public TS() {

	}

	/**
	 * constructor of GA
	 * 
	 * @param n
	 *          ��������
	 * @param g
	 *          ���д���
	 * @param c
	 *          ÿ�������ھӸ���
	 * @param m
	 *          ���ɳ���
	 * 
	 **/
	public TS(int n, int g, int c, int m) {
		nodeNum = n;
		MAX_GEN = g;
		neighbourhoodNum = c;
		tabuTableLength = m;
	}

	/**
	 * ��ʼ��Tabu�㷨��
	 * 
	 * @param filename
	 *                 �����ļ��������ļ��洢���г��нڵ���������
	 * @throws IOException
	 */
	public void init(String filename) throws IOException {
		// ��ȡ����
		int[] x;
		int[] y;
		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		nodeDistance = new int[nodeNum][nodeNum];
		x = new int[nodeNum];
		y = new int[nodeNum];
		while ((strbuff = data.readLine()) != null) {
			if (!Character.isAlphabetic(strbuff.charAt(0)))
				break;
		}
		String[] tmp = strbuff.split(" ");
		x[0] = Integer.valueOf(tmp[1]);// x����
		y[0] = Integer.valueOf(tmp[2]);// y����
		for (int i = 1; i < nodeNum; i++) {
			strbuff = data.readLine();
			String[] strcol = strbuff.split(" ");
			x[i] = Integer.valueOf(strcol[1]).intValue();
			y[i] = Integer.valueOf(strcol[2]).intValue();
		}
		// String[] strcol;
		// for (int i = 0; i < nodeNum; i++) {
		// // ��ȡһ�����ݣ����ݸ�ʽ1 6734 1453
		// strbuff = list.get(i);
		// // �ַ��ָ�
		// strcol = strbuff.split(" ");
		// x[i] = Integer.valueOf(strcol[1]);// x����
		// y[i] = Integer.valueOf(strcol[2]);// y����
		// }
		// ����������
		// ����Ծ������⣬������㷽��Ҳ��һ�����˴��õ���att48��Ϊ����������48�����У�������㷽��Ϊαŷ�Ͼ��룬����ֵΪ10628
		for (int i = 0; i < nodeNum - 1; i++) {
			nodeDistance[i][i] = 0; // �Խ���Ϊ0
			for (int j = i + 1; j < nodeNum; j++) {
				double rij = Math.sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j])) / 10.0);
				// �������룬ȡ��
				int tij = (int) Math.round(rij);
				if (tij < rij) {
					nodeDistance[i][j] = tij + 1;
					nodeDistance[j][i] = nodeDistance[i][j];
				} else {
					nodeDistance[i][j] = tij;
					nodeDistance[j][i] = nodeDistance[i][j];
				}
			}
		}
		nodeDistance[nodeNum - 1][nodeNum - 1] = 0;

		route = new int[nodeNum];
		bestRoute = new int[nodeNum];
		bestEvaluation = Integer.MAX_VALUE;
		tabuTable = new int[tabuTableLength][nodeNum];
		tabuTableEvaluate = new int[tabuTableLength];
		for (int i = 0; i < tabuTableEvaluate.length; i++) {
			tabuTableEvaluate[i] = Integer.MAX_VALUE;
		}
	}

	/** ���ɳ�ʼȺ�� */
	void generateInitGroup() {
		System.out.println("1.���ɳ�ʼȺ��");
		boolean iscontinue = false;
		for (int i = 0; i < route.length; i++) {
			do {
				iscontinue = false;
				route[i] = (int) (Math.random() * nodeNum);
				for (int j = i - 1; j >= 0; j--) {
					if (route[i] == route[j]) {
						iscontinue = true;
						break;
					}
				}
			} while (iscontinue);
			// System.out.println("i="+i+", route[i]="+route[i]);
		}
	}

	/** ���Ʊ����壬����Gha��Ghb */
	public void copyGh(int[] Gha, int[] Ghb) {
		for (int i = 0; i < nodeNum; i++) {
			Ghb[i] = Gha[i];
		}
	}

	/** ����·�ߵ��ܾ��� */
	public int evaluate(int[] chr) {
		// 0123
		int len = 0;
		// ���룬��ʼ����,����1,����2...����n
		for (int i = 1; i < nodeNum; i++) {
			len += nodeDistance[chr[i - 1]][chr[i]];
		}
		// ����n,��ʼ����
		len += nodeDistance[chr[nodeNum - 1]][chr[0]];
		return len;
	}

	/**
	 * �����ȡ����·��
	 * 
	 * @param route
	 *              ��ǰ·��
	 */
	public int[] getNeighbourhood(int[] route) {
		int temp;
		int ran1, ran2;
		int[] tempRoute = new int[route.length];
		copyGh(route, tempRoute);
		ran1 = (int) (Math.random() * nodeNum);
		do {
			ran2 = (int) (Math.random() * nodeNum);
		} while (ran1 == ran2);
		temp = tempRoute[ran1];
		tempRoute[ran1] = tempRoute[ran2];
		tempRoute[ran2] = temp;
		return tempRoute;
	}

	/**
	 * �����ȡһ������������·��
	 */
	public int[][] getNeighbourhood(int[] route, int tempNeighbourhoodNum) {
		int[][] NeighbourhoodRoutes = new int[tempNeighbourhoodNum][nodeNum];
		List<int[]> tempExchangeNodeList = new ArrayList<>();
		int temp;
		int ran0, ran1;
		int[] tempRoute = null;
		boolean iscontinue;
		for (int i = 0; i < tempNeighbourhoodNum; i++) {
			tempRoute = new int[route.length];
			copyGh(route, tempRoute);
			do {
				iscontinue = false;
				// �������һ������;
				ran0 = (int) (Math.random() * nodeNum);
				do {
					ran1 = (int) (Math.random() * nodeNum);
				} while (ran0 == ran1);
				// �ж��Ƿ��ظ�
				for (int j = 0; j < tempExchangeNodeList.size(); j++) {
					if (tempExchangeNodeList.get(j)[0] < tempExchangeNodeList.get(j)[1]) {
						if ((ran0 < ran1
								&& (tempExchangeNodeList.get(j)[0] == ran0 && tempExchangeNodeList.get(j)[1] == ran1))
								|| (ran0 > ran1 && (tempExchangeNodeList.get(j)[0] == ran1
										&& tempExchangeNodeList.get(j)[1] == ran0))) {
							iscontinue = true;
						}
					} else {
						if ((ran0 < ran1
								&& (tempExchangeNodeList.get(j)[0] == ran1 && tempExchangeNodeList.get(j)[1] == ran0))
								|| (ran0 > ran1 && (tempExchangeNodeList.get(j)[0] == ran0
										&& tempExchangeNodeList.get(j)[1] == ran1))) {
							iscontinue = true;
						}
					}
				}
				if (iscontinue == false) {
					temp = tempRoute[ran0];
					tempRoute[ran0] = tempRoute[ran1];
					tempRoute[ran1] = temp;
					tempExchangeNodeList.add(new int[] { ran0, ran1 });// �����������ӵ��б������ڲ��أ�
					// �ж��Ƿ���route��ͬ
					for (int j = 0; j < tempRoute.length; j++) {
						if (tempRoute[j] != route[j]) {
							iscontinue = false;
						}
					}
					if (iscontinue == false && !isInTabuTable(tempRoute)) {
						NeighbourhoodRoutes[i] = tempRoute;
					} else {
						iscontinue = true;
					}
				}
			} while (iscontinue);
		}
		return NeighbourhoodRoutes;
	}

	/** �ж�·���Ƿ��ڽ��ɱ��� */
	public boolean isInTabuTable(int[] tempRoute) {
		int i, j;
		int flag = 0;
		for (i = 0; i < tabuTableLength; i++) {
			flag = 0;
			for (j = 0; j < nodeNum; j++) {
				if (tempRoute[j] != tabuTable[i][j]) {
					flag = 1;// ����ͬ
					break;
				}
			}
			if (flag == 0) {// ��ͬ�����ش�����ͬ
				break;
			}
		}
		if (i == tabuTableLength) {// ����
			return false;// ������
		} else {
			return true;// ����
		}
	}

	/** ������������ɣ�ע����ɲ��Ե�ѡ�� */
	public void flushTabuTable(int[] tempGh) {
		int tempValue = evaluate(tempGh);
		// �ҵ����ɱ���·�������ֵ��
		int tempMax = tabuTableEvaluate[0];
		int maxValueIndex = 0;
		for (int i = 0; i < tabuTableLength; i++) {
			if (tabuTableEvaluate[i] > tempMax) {
				tempMax = tabuTableEvaluate[i];
				maxValueIndex = i;
			}
		}
		// �µ�·��������ɱ�
		if (tempValue < tabuTableEvaluate[maxValueIndex]) {
			if (tabuTableEvaluate[maxValueIndex] < Integer.MAX_VALUE) {
				copyGh(tabuTable[maxValueIndex], route);
			}
			System.out.println("���Ե㣺���½��ɱ���maxValueIndex= " + maxValueIndex);
			for (int k = 0; k < nodeNum; k++) {
				tabuTable[maxValueIndex][k] = tempGh[k];
			}
			tabuTableEvaluate[maxValueIndex] = tempValue;
		}
	}

	/** ������������ */
	public void startSearch() {
		int nn;
		int neighbourhoodEvaluation;
		int currentBestRouteEvaluation;
		/** �������·�� */
		int[] neighbourhoodOfRoute = new int[nodeNum];
		/** �������·�� */
		int[] currentBestRoute = new int[nodeNum];
		/** ��ǰ���� */
		int currentIterateNum = 0;
		/** ��ѳ��ִ��� */
		int bestIterateNum = 0;
		int[][] neighbourhoodOfRoutes = null;
		// ���ڿ��Ƶ�������
		int[] priviousRoute = new int[nodeNum];
		// ��ʼ������Ghh
		generateInitGroup();
		// ����ǰ·����Ϊ���·��
		copyGh(route, bestRoute);
		currentBestRouteEvaluation = evaluate(route);
		bestEvaluation = currentBestRouteEvaluation;
		System.out.println("2.��������....");
		while (currentIterateNum < MAX_GEN) {
			for (int i = 0; i < route.length; i++) {
				priviousRoute[i] = route[i];
			}
			neighbourhoodOfRoutes = getNeighbourhood(route, neighbourhoodNum);
			System.out.println("���Ե㣺currentIterateNum= " + currentIterateNum);
			for (nn = 0; nn < neighbourhoodNum; nn++) {
				// �õ���ǰ·��route��һ������·��neighbourhoodOfRoute
				// neighbourhoodOfRoute=getNeighbourhood(route);
				neighbourhoodOfRoute = neighbourhoodOfRoutes[nn];
				neighbourhoodEvaluation = evaluate(neighbourhoodOfRoute);
				// System.out.println("���ԣ�neighbourhoodOfRoute="+neighbourhoodEvaluation);
				if (neighbourhoodEvaluation < currentBestRouteEvaluation) {
					copyGh(neighbourhoodOfRoute, currentBestRoute);
					currentBestRouteEvaluation = neighbourhoodEvaluation;
					// System.out.println("���ԣ�neighbourhoodOfRoute="+neighbourhoodEvaluation);
				}
			}
			if (currentBestRouteEvaluation < bestEvaluation) {
				bestIterateNum = currentIterateNum;
				copyGh(currentBestRoute, bestRoute);
				bestEvaluation = currentBestRouteEvaluation;
				System.out.println("���ԣ�currentBestRouteEvaluation=" + currentBestRouteEvaluation);
			}
			copyGh(currentBestRoute, route);
			// ����ɱ���currentBestRoute������ɱ�
			// System.out.println("���Ե㣺currentBestRoute= "+currentBestRoute);
			flushTabuTable(currentBestRoute);
			currentIterateNum++;
			for (int i = 0; i < priviousRoute.length; i++) {
				if (priviousRoute[i] != route[i]) {
					currentIterateNum = 0;
					break;
				}
			}

			printRunStatus();
		}

		// �����ʾ��
		System.out.println("��ѳ��ȳ��ִ�����");
		System.out.println(bestIterateNum);
		System.out.println("��ѳ���:");
		System.out.println(bestEvaluation);
		System.out.println("���·����");
		for (int i = 0; i < nodeNum; i++) {
			System.out.print(bestRoute[i] + ",");
		}
	}

	/**
	 * @Description: ���������״̬
	 */
	private void printRunStatus() {
		System.out.println("����·�����ȣ�" + bestEvaluation);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Start....");
		List<String> listSearch = new ArrayList<String>();
		TS tabu = new TS(48, 120, 500, 5);
		tabu.init("resources/att48.tsp");
		tabu.startSearch();
	}
}