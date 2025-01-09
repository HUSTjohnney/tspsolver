package com;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TSPGenerator {
    public static void generateTSPInstance(int numNodes, String filePath) {
        Random random = new Random();
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("NAME : auto_generated\n");
            writer.write("COMMENT : Automatically generated TSP instance\n");
            writer.write("TYPE : TSP\n");
            writer.write("DIMENSION : " + numNodes + "\n");
            writer.write("EDGE_WEIGHT_TYPE : EUC_2D\n");
            writer.write("NODE_COORD_SECTION\n");
            for (int i = 1; i <= numNodes; i++) {
                int x = random.nextInt(101);
                int y = random.nextInt(101);
                writer.write(i + " " + x + " " + y + "\n");
            }
            writer.write("EOF\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 随机生成20个算例
        for (int i = 1; i <= 20; i++) {
            String fileName = "p" + String.format("%02d", i);
            generateTSPInstance(50, "src\\main\\resources\\52nodes\\" + fileName + ".txt");
        }
    }
}