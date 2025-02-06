package ttc;

import java.util.Arrays;

public class InputData {
    private final int numTasks;
    private final int numArcs;
    private final double[] phi;
    private final double[] delta;
    private final int[][] d;
    private final int[] o;
    private final int[] ST;
    private final int[] ET;

    public InputData(int numTasks, int numArcs, double[] phi, double[] delta, int[][] d, int[] o, int[] ST, int[] ET) {
        this.numTasks = numTasks;
        this.numArcs = numArcs;
        this.phi = Arrays.copyOf(phi, phi.length);
        this.delta = Arrays.copyOf(delta, delta.length);
        this.d = new int[d.length][];
        for (int i = 0; i < d.length; i++) {
            this.d[i] = Arrays.copyOf(d[i], d[i].length);
        }
        this.o = Arrays.copyOf(o, o.length);
        this.ST = Arrays.copyOf(ST, ST.length);
        this.ET = Arrays.copyOf(ET, ET.length);
    }

    public int getNumTasks() {
        return numTasks;
    }

    public int getNumArcs() {
        return numArcs;
    }

    public double[] getPhi() {
        return Arrays.copyOf(phi, phi.length);
    }

    public double[] getDelta() {
        return Arrays.copyOf(delta, delta.length);
    }

    public int[][] getD() {
        int[][] copy = new int[d.length][];
        for (int i = 0; i < d.length; i++) {
            copy[i] = Arrays.copyOf(d[i], d[i].length);
        }
        return copy;
    }

    public int[] getO() {
        return Arrays.copyOf(o, o.length);
    }

    public int[] getST() {
        return Arrays.copyOf(ST, ST.length);
    }

    public int[] getET() {
        return Arrays.copyOf(ET, ET.length);
    }
}