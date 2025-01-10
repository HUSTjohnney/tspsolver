package com.aco;

import java.util.Random;
import java.util.Vector;

public class Ant implements Cloneable {

    private Vector<Integer> tabu; // 禁忌表
    private Vector<Integer> allowedCities; // 允许搜索的城市
    private float[][] delta; // 信息数变化矩阵
    private int[][] dist; // 距离矩阵

    private final float ALPHA; // 信息启发因子
    private final float BETA; // 期望启发因子

    private final int cityNum; // 蚂蚁所需走的城市数量
    private final int firstCity; // 蚂蚁所在的起始城市

    private int routeLength; // 路径长度
    private int currentCity; // 当前城市

    /**
     * Constructor of Ant
     * 
     * @param citynum 蚂蚁数量
     */
    public Ant(int citynum, float alpha, float beta) {
        cityNum = citynum;
        routeLength = Integer.MAX_VALUE;
        // 随机挑选一个城市作为起始城市
        Random random = new Random(System.currentTimeMillis());
        firstCity = random.nextInt(cityNum);
        this.ALPHA = alpha;
        this.BETA = beta;
    }

    /**
     * 初始化蚂蚁，随机选择起始位置
     * 
     * @param distance 距离矩阵
     * @param alpha    信息启发因子
     * @param beta     期望启发因子
     */

    public void init(int[][] distance) {
        // 初始允许搜索的城市集合
        allowedCities = new Vector<Integer>();
        // 初始禁忌表
        tabu = new Vector<Integer>();
        // 初始距离矩阵
        this.dist = distance;
        // 初始信息数变化矩阵为0
        delta = new float[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            allowedCities.add(i);
            for (int j = 0; j < cityNum; j++) {
                delta[i][j] = 0.f;
            }
        }

        // 允许搜索的城市集合中移除起始城市
        for (Integer i : allowedCities) {
            if (i.intValue() == firstCity) {
                allowedCities.remove(i);
                break;
            }
        }
        // 将起始城市添加至禁忌表
        tabu.add(Integer.valueOf(firstCity));
        // 当前城市为起始城市
        currentCity = firstCity;
    }

    /**
     * 
     * 选择下一个城市
     * 
     * @param pheromone 信息素矩阵
     */

    public void selectNextCity(float[][] pheromone) {
        float[] p = new float[cityNum];
        float sum = 0.0f;
        // 计算分母部分
        for (Integer i : allowedCities) {
            sum += Math.pow(pheromone[currentCity][i.intValue()], ALPHA)
                    * Math.pow(1.0 / dist[currentCity][i.intValue()], BETA);
        }
        // 计算概率矩阵
        for (int i = 0; i < cityNum; i++) {
            boolean flag = false;
            for (Integer j : allowedCities) {
                if (i == j.intValue()) {
                    p[i] = (float) (Math.pow(pheromone[currentCity][i], ALPHA) * Math
                            .pow(1.0 / dist[currentCity][i], BETA)) / sum;
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                p[i] = 0.f;
            }
        }
        // 轮盘赌选择下一个城市
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
        // 从允许选择的城市中去除select city
        for (Integer i : allowedCities) {
            if (i.intValue() == selectCity) {
                allowedCities.remove(i);
                break;
            }
        }
        // 在禁忌表中添加select city
        tabu.add(Integer.valueOf(selectCity));
        // 将当前城市改为选择的城市
        currentCity = selectCity;
    }

    /**
     * 计算路径长度
     * 
     * @return 路径长度
     */
    private int calculateTourLength() {
        int len = 0;
        // 禁忌表tabu最终形式：起始城市,城市1,城市2...城市n,起始城市
        for (int i = 0; i < cityNum; i++) {
            len += dist[this.tabu.get(i).intValue()][this.tabu.get(i + 1)
                    .intValue()];
        }
        return len;
    }

    public Vector<Integer> getAllowedCities() {
        return allowedCities;
    }

    public void setAllowedCities(Vector<Integer> allowedCities) {
        this.allowedCities = allowedCities;
    }

    public int getRouteLength() {
        routeLength = calculateTourLength();
        return routeLength;
    }

    public void setRouteLength(int tourLength) {
        this.routeLength = tourLength;
    }

    public int getCityNum() {
        return cityNum;
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

}
