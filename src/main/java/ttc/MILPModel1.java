package ttc;

import ilog.concert.*;
import ilog.cplex.*;

public class MILPModel1 {
    private final InputData inputData;

    private IloCplex cplex;
    private IloIntVar[][] x;
    private IloNumVar[][] s;
    private IloIntVar[] z;

    public MILPModel1(InputData inputData) {
        this.inputData = inputData;
    }

    public void buildModel() throws IloException {
        cplex = new IloCplex();
        int numTasks = inputData.getNumTasks();
        int numArcs = inputData.getNumArcs();

        // 定义决策变量
        x = new IloIntVar[numTasks][numArcs];
        s = new IloNumVar[numTasks][numArcs];
        z = new IloIntVar[numArcs];

        for (int i = 0; i < numTasks; i++) {
            for (int k = 0; k < numArcs; k++) {
                x[i][k] = cplex.boolVar("x_" + i + "_" + k);
                s[i][k] = cplex.numVar(0, Double.MAX_VALUE, "s_" + i + "_" + k);
            }
        }

        for (int k = 0; k < numArcs; k++) {
            z[k] = cplex.boolVar("z_" + k);
        }

        // 目标函数
        IloLinearNumExpr obj = cplex.linearNumExpr();
        double[] phi = inputData.getPhi();
        for (int k = 0; k < numArcs; k++) {
            obj.addTerm(phi[k], z[k]);
        }

        double[] delta = inputData.getDelta();
        for (int i = 0; i < numTasks; i++) {
            IloLinearNumExpr si = cplex.linearNumExpr();
            for (int k = 0; k < numArcs; k++) {
                si.addTerm(delta[i], s[i][k]);
            }
            obj.add(si);
        }
        cplex.addMinimize(obj);

        // 约束条件
        addConstraints();
    }

    private void addConstraints() throws IloException {
        int numTasks = inputData.getNumTasks();
        int numArcs = inputData.getNumArcs();
        int[][] d = inputData.getD();
        int[] o = inputData.getO();
        int[] ST = inputData.getST();
        int[] ET = inputData.getET();

        // 约束(1): 每个任务至少被一个测控弧段进行服务
        for (int i = 0; i < numTasks; i++) {
            IloLinearNumExpr sumX = cplex.linearNumExpr();
            for (int k = 0; k < numArcs; k++) {
                sumX.addTerm(1, x[i][k]);
            }
            cplex.addEq(sumX, 1);
        }

        // 约束(2): 每个测控弧段最多服务一个任务
        for (int k = 0; k < numArcs; k++) {
            IloLinearNumExpr sumX = cplex.linearNumExpr();
            for (int i = 0; i < numTasks; i++) {
                sumX.addTerm(1, x[i][k]);
            }
            cplex.addLe(sumX, 1);
        }

        // 约束(3): 决策变量传递约束
        double M = 1000;
        for (int k = 0; k < numArcs; k++) {
            IloLinearNumExpr sumZ = cplex.linearNumExpr();
            for (int i = 0; i < numTasks; i++) {
                sumZ.addTerm(1, x[i][k]);
            }
            cplex.addLe(sumZ, cplex.prod(z[k], M));
        }

        // 约束(4)和(5): 任务的开始时刻变量传递约束
        for (int i = 0; i < numTasks; i++) {
            for (int k = 0; k < numArcs; k++) {
                cplex.addLe(
                        cplex.sum(
                                s[i][k],
                                cplex.prod(d[i][k], x[i][k]),
                                cplex.prod(cplex.sum(x[i][k], -1), M)),
                        ET[k]);
                cplex.addGe(
                        cplex.sum(
                                s[i][k],
                                cplex.prod(cplex.sum(-1, x[i][k]), -M)),
                        ST[k] + o[i]);
            }
        }
    }

    public boolean solve() throws IloException {
        return cplex.solve();
    }

    public double getObjectiveValue() throws IloException {
        return cplex.getObjValue();
    }

    public double getVariableValue(IloNumVar s2) throws IloException {
        return cplex.getValue(s2);
    }

    public IloIntVar[][] getX() {
        return x;
    }

    public IloNumVar[][] getS() {
        return s;
    }

    public IloIntVar[] getZ() {
        return z;
    }

    public InputData getInputData() {
        return inputData;
    }

    public void end() {
        cplex.end();
    }
}