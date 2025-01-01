package com.ACO;

import java.util.Random;
import java.util.Vector;

public class Ant implements Cloneable {

	private Vector<Integer> tabu; // ���ɱ�
	private Vector<Integer> allowedCities; // ���������ĳ���
	private float[][] delta; // ��Ϣ���仯����
	private int[][] distance; // �������

	private float alpha;
	private float beta;

	private int tourLength; // ·������
	private int cityNum; // ��������

	private int firstCity; // ��ʼ����
	private int currentCity; // ��ǰ����

	public Ant() {
		cityNum = 30;
		tourLength = 0;

	}

	/**
	 * Constructor of Ant
	 * 
	 * @param num
	 *            ��������
	 */
	public Ant(int num) {
		cityNum = num;
		tourLength = 0;

	}

	/**
	 * ��ʼ�����ϣ����ѡ����ʼλ��
	 * 
	 * @param distance
	 *            �������
	 * @param a
	 *            alpha
	 * @param b
	 *            beta
	 */
	public void init(int[][] distance, float a, float b) {
		alpha = a;
		beta = b;
		allowedCities = new Vector<Integer>();
		tabu = new Vector<Integer>();
		this.distance = distance;
		delta = new float[cityNum][cityNum];
		for (int i = 0; i < cityNum; i++) {
			Integer integer = new Integer(i);
			allowedCities.add(integer);
			for (int j = 0; j < cityNum; j++) {
				delta[i][j] = 0.f;
			}
		}

		Random random = new Random(System.currentTimeMillis());
		firstCity = random.nextInt(cityNum);
		for (Integer i : allowedCities) {
			if (i.intValue() == firstCity) {
				allowedCities.remove(i);
				break;
			}
		}

		tabu.add(Integer.valueOf(firstCity));
		currentCity = firstCity;
	}

	/**
	 * ѡ����һ������
	 * 
	 * @param pheromone
	 *            ��Ϣ�ؾ���
	 */
	public void selectNextCity(float[][] pheromone) {
		float[] p = new float[cityNum];
		float sum = 0.0f;
		// �����ĸ����
		for (Integer i : allowedCities) {
			sum += Math.pow(pheromone[currentCity][i.intValue()], alpha)
					* Math.pow(1.0 / distance[currentCity][i.intValue()], beta);
		}
		// ������ʾ���
		for (int i = 0; i < cityNum; i++) {
			boolean flag = false;
			for (Integer j : allowedCities) {

				if (i == j.intValue()) {
					p[i] = (float) (Math.pow(pheromone[currentCity][i], alpha)
							* Math.pow(1.0 / distance[currentCity][i], beta)) / sum;
					flag = true;
					break;
				}
			}

			if (flag == false) {
				p[i] = 0.f;
			}
		}

		// ���̶�ѡ����һ������
		Random random = new Random(System.currentTimeMillis());
		float sleectP = random.nextFloat();
		int selectCity = 0;
		float sum1 = 0.f;
		for (int i = 0; i < cityNum; i++) {
			sum1 += p[i];
			if (sum1 >= sleectP) {
				selectCity = i;
				break;
			}
		}

		// ������ѡ��ĳ�����ȥ��select city
		for (Integer i : allowedCities) {
			if (i.intValue() == selectCity) {
				allowedCities.remove(i);
				break;
			}
		}
		// �ڽ��ɱ�������select city
		tabu.add(Integer.valueOf(selectCity));
		// ����ǰ���и�Ϊѡ��ĳ���
		currentCity = selectCity;

	}

	/**
	 * ����·������
	 * 
	 * @return ·������
	 */
	private int calculateTourLength() {
		int len = 0;
		for (int i = 0; i < cityNum; i++) {
			len += distance[this.tabu.get(i).intValue()][this.tabu.get(i + 1).intValue()];
		}
		return len;
	}

	public Vector<Integer> getAllowedCities() {
		return allowedCities;
	}

	public void setAllowedCities(Vector<Integer> allowedCities) {
		this.allowedCities = allowedCities;
	}

	public int getTourLength() {
		tourLength = calculateTourLength();
		return tourLength;
	}

	public void setTourLength(int tourLength) {
		this.tourLength = tourLength;
	}

	public int getCityNum() {
		return cityNum;
	}

	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
	}

	public Vector<Integer> getTabu() {
		return tabu;
	}

	public void setTabu(Vector<Integer> tabu) {
		this.tabu = tabu;
	}

	public float[][] getDelta() {
		return delta;
	}

	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	public int getFirstCity() {
		return firstCity;
	}

	public void setFirstCity(int firstCity) {
		this.firstCity = firstCity;
	}

}