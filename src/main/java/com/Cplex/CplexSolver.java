package com.Cplex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.TspPlan;
import com.TspProblem;
import com.sa.SA;

public class CplexSolver {
    public static String line = "-------------------------------------------------------------------------";

    public static void main(String[] args) throws IOException {

        // Random random = new Random(666L);
        // for (int cityNum = 30; cityNum <= 40; cityNum++) {
        // solveByMip(cityNum, random);
        // }

        TspProblem problem = TspProblem.read("src\\main\\resources\\eil51.txt", 51);
        long startTime = System.currentTimeMillis();
        new IP_TSP(problem).solve();
        long endTime = System.currentTimeMillis();
        System.out.println("solveTime: " + ((endTime - startTime) / 1000d) + " s");
        
    }

    public static void solveByMip(int cityNum, Random random) {
        long startTime = System.currentTimeMillis();
        List<double[]> locationList = createLocationList(cityNum, random);
        new IP_TSP(locationList).solve();
        System.out.println(
                "cityNum: " + cityNum + " , solveTime: " + ((System.currentTimeMillis() - startTime) / 1000d) + " s");
        System.out.println(line);
    }

    public static List<double[]> createLocationList(int cityNum, Random random) {
        List<double[]> locationList = new ArrayList<>();
        for (int i = 0; i < cityNum; i++) {
            locationList.add(new double[] { random.nextInt(100), random.nextInt(100) });
        }
        return locationList;
    }

}
