package com.pso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import com.TSPUtils;
import com.TspProblem;

public class PSO {

	private int bestNum;
	private float w;
	private int MAX_GEN;// the times of evolution
	private int scale;// the scale of population

	private int cityNum; // the number of city
	private int t;// current generation

	private int[][] distance; // distance matrix

	private int[][] oPopulation;// particle swarm
	private ArrayList<ArrayList<SO>> listV;// the initial exchange order of every particle

	private int[][] Pd;// the best solution of a particle in the past��
	private int[] vPd;// the value of the solution

	private int[] Pgd;// the best solution of the whole particle swarm in the past
	private int vPgd;// the value of the best solution
	private int bestT;// the best generation

	private int[] fitness;// the fitness of population

	private Random random;

	public PSO() {

	}

	/**
	 * constructor of GA
	 * 
	 * @param n the number of city
	 * 
	 * @param g evolutionary generation
	 * 
	 * @param s the scale of population
	 * 
	 * @param w weight
	 * 
	 **/
	public PSO(int n, int g, int s, float w) {
		this.cityNum = n;
		this.MAX_GEN = g;
		this.scale = s;
		this.w = w;
	}

	// ��������һ��ָ��������Ա���ע�Ĵ���Ԫ���ڲ���ĳЩ���汣�־�Ĭ
	@SuppressWarnings("resource")
	/**
	 * ��ʼ��PSO�㷨��
	 * 
	 * @param filename �����ļ��������ļ��洢���г��нڵ���������
	 * @throws IOException
	 */
	private void init(String filename) throws IOException {
		// read the data
		TspProblem problem = TSPUtils.read(filename, cityNum);
		this.distance = problem.getDist();

		oPopulation = new int[scale][cityNum];
		fitness = new int[scale];

		Pd = new int[scale][cityNum];
		vPd = new int[scale];

		Pgd = new int[cityNum];
		vPgd = Integer.MAX_VALUE;

		bestT = 0;
		t = 0;

		random = new Random(System.currentTimeMillis());

	}

	// initial population
	void initGroup() {
		int i, j, k;
		for (k = 0; k < scale; k++)// the number of population
		{
			oPopulation[k][0] = random.nextInt(65535) % cityNum;
			for (i = 1; i < cityNum;)// the number of particle
			{
				oPopulation[k][i] = random.nextInt(65535) % cityNum;
				for (j = 0; j < i; j++) {
					if (oPopulation[k][i] == oPopulation[k][j]) {
						break;
					}
				}
				if (j == i) {
					i++;
				}
			}
		}

	}

	void initListV() {
		int ra;
		int raA;
		int raB;

		listV = new ArrayList<ArrayList<SO>>();

		for (int i = 0; i < scale; i++) {
			ArrayList<SO> list = new ArrayList<SO>();
			ra = random.nextInt(65535) % cityNum;
			for (int j = 0; j < ra; j++) {
				raA = random.nextInt(65535) % cityNum;
				raB = random.nextInt(65535) % cityNum;
				while (raA == raB) {
					raB = random.nextInt(65535) % cityNum;
				}

				// raA��raB��һ��
				SO s = new SO(raA, raB);
				list.add(s);
			}

			listV.add(list);
		}
	}

	public int evaluate(int[] chr) {
		// 0123
		int len = 0;
		// ���룬��ʼ����,����1,����2...����n
		for (int i = 1; i < cityNum; i++) {
			len += distance[chr[i - 1]][chr[i]];
		}
		// ����n,��ʼ����
		len += distance[chr[cityNum - 1]][chr[0]];
		return len;
	}

	// ��һ�������������������ڱ���arr��ı���
	public void add(int[] arr, ArrayList<SO> list) {
		int temp = -1;
		SO s;
		for (int i = 0; i < list.size(); i++) {
			s = list.get(i);
			temp = arr[s.getX()];
			arr[s.getX()] = arr[s.getY()];
			arr[s.getY()] = temp;
		}
	}

	// ����������Ļ����������У���A-B=SS
	public ArrayList<SO> minus(int[] a, int[] b) {
		int[] temp = b.clone();
		/*
		 * int[] temp=new int[L]; for(int i=0;i<L;i++) { temp[i]=b[i]; }
		 */
		int index;
		// Commutants
		SO s;
		// exchange order
		ArrayList<SO> list = new ArrayList<SO>();
		for (int i = 0; i < cityNum; i++) {
			if (a[i] != temp[i]) {
				// ��temp���ҳ���a[i]��ͬ��ֵ���±�index
				index = findNum(temp, a[i]);
				// exchange the value between the index of i and index in the temp
				changeIndex(temp, i, index);
				// remember Commutants
				s = new SO(i, index);
				// save Commutants
				list.add(s);
			}
		}
		return list;
	}

	// find the num in the array arr,return the index of num
	public int findNum(int[] arr, int num) {
		int index = -1;
		for (int i = 0; i < cityNum; i++) {
			if (arr[i] == num) {
				index = i;
				break;
			}
		}
		return index;
	}

	// exchange the index between index1 and index2 in the array arr
	public void changeIndex(int[] arr, int index1, int index2) {
		int temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
	}

	// two-dimensional array copy
	public void copyarray(int[][] from, int[][] to) {
		for (int i = 0; i < scale; i++) {
			for (int j = 0; j < cityNum; j++) {
				to[i][j] = from[i][j];
			}
		}
	}

	// One-dimensional array copy
	public void copyarrayNum(int[] from, int[] to) {
		for (int i = 0; i < cityNum; i++) {
			to[i] = from[i];
		}
	}

	public void evolution() {
		int i, j, k;
		int len = 0;
		float ra = 0f;

		ArrayList<SO> Vi;

		// evolution once
		for (t = 0; t < MAX_GEN; t++) {
			// for every particle
			for (i = 0; i < scale; i++) {
				if (i == bestNum)
					continue;
				ArrayList<SO> Vii = new ArrayList<SO>();
				// System.out.println("------------------------------");
				// update the speed
				// Vii=wVi+ra(Pid-Xid)+rb(Pgd-Xid)
				Vi = listV.get(i);

				// wVi+��ʾ��ȡVi��size*wȡ������������
				len = (int) (Vi.size() * w);
				// Խ���ж�
				// if(len>cityNum) len=cityNum;
				// System.out.println("w:"+w+" len:"+len+" Vi.size():"+Vi.size());
				for (j = 0; j < len; j++) {
					Vii.add(Vi.get(j));
				}

				// Pid-Xid
				ArrayList<SO> a = minus(Pd[i], oPopulation[i]);
				ra = random.nextFloat();

				// ra(Pid-Xid)+
				len = (int) (a.size() * ra);
				// Խ���ж�

				for (j = 0; j < len; j++) {
					Vii.add(a.get(j));
				}

				// Pid-Xid
				ArrayList<SO> b = minus(Pgd, oPopulation[i]);
				ra = random.nextFloat();

				// ra(Pid-Xid)+
				len = (int) (b.size() * ra);
				// Խ���ж�
				// if(len>cityNum) len=cityNum;
				// System.out.println("ra:"+ra+" len:"+len+" b.size():"+b.size());
				for (j = 0; j < len; j++) {
					SO tt = b.get(j);
					Vii.add(tt);
				}

				// preserve the new Vii
				listV.add(i, Vii);

				// update the position
				// Xid��=Xid+Vid
				add(oPopulation[i], Vii);
			}

			// calculte the fitness[k] of new particle ,select the best solution
			for (k = 0; k < scale; k++) {
				fitness[k] = evaluate(oPopulation[k]);
				if (vPd[k] > fitness[k]) {
					vPd[k] = fitness[k];
					copyarrayNum(oPopulation[k], Pd[k]);
					bestNum = k;
				}
				if (vPgd > vPd[k]) {
					System.out.println("bestLength" + vPgd + " generation��" + bestT);
					bestT = t;
					vPgd = vPd[k];
					copyarrayNum(Pd[k], Pgd);
				}
			}
		}
	}

	public void solve() {
		int i;
		int k;

		initGroup();
		initListV();

		// every particle remember the own best solution
		copyarray(oPopulation, Pd);

		// calculate the fitness[k] of initial population,Fitness[max],select the best
		// solution
		for (k = 0; k < scale; k++) {
			fitness[k] = evaluate(oPopulation[k]);
			vPd[k] = fitness[k];
			if (vPgd > vPd[k]) {
				vPgd = vPd[k];
				copyarrayNum(Pd[k], Pgd);
				bestNum = k;
			}
		}

		System.out.println("initial particle swarm...");
		for (k = 0; k < scale; k++) {
			for (i = 0; i < cityNum; i++) {
				System.out.print(oPopulation[k][i] + ",");
			}
			System.out.println();
			System.out.println("----" + fitness[k]);

		}

		// evoluton
		evolution();

		System.out.println("final particle swarm...");
		for (k = 0; k < scale; k++) {
			for (i = 0; i < cityNum; i++) {
				System.out.print(oPopulation[k][i] + ",");
			}
			System.out.println();
			System.out.println("----" + fitness[k]);

		}

		System.out.print("bestT:");
		System.out.println(bestT);
		System.out.print("bestLength");
		System.out.println(vPgd);
		System.out.println("bestTour��");
		System.out.print(Pgd[0]);
		for (i = 0; i < cityNum; i++) {
			System.out.print("->" + Pgd[i]);
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Start....");

		PSO pso = new PSO(48, 1000, 20, 0.5f);
		pso.init("src\\main\\resources\\eil51.txt");
		pso.solve();
	}
}
