package com;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class TspReaderTest {
    @Test
    void testRead() {
        TspProblem problem;
        try {
            problem = TSPUtils.read("resources/eil23.txt");
            int[] x = problem.getxCoors();
            for (int i = 0; i < x.length; i++) {
                System.out.println(x[i]);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
