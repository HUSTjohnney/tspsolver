package com.Cplex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.TSPUtils;
import com.TspPlan;
import com.TspProblem;
import com.TspSolver;

import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class CplexSolver implements TspSolver {

    private TspProblem problem;

    // 城市坐标<[x,y]>
    private List<int[]> locationList;

    /**
     * 构造函数，初始化城市坐标
     * 
     * @param problem TSP问题
     */
    public CplexSolver(TspProblem problem) {
        this.problem = problem;
        this.locationList = new ArrayList<>();
        for (int i = 0; i < problem.getxCoors().length; i++) {
            locationList.add(new int[] { problem.getxCoors()[i], problem.getyCoors()[i] });
        }
    }

    public static void main(String[] args) throws IOException {

        TspProblem problem = TSPUtils.read("src\\main\\resources\\eil51.txt", 51);
        TspPlan p = new CplexSolver(problem).solve();
        System.out.println("Cplex: " + p);

    }

    public TspPlan solve() {
        int cityNum = locationList.size();
        int[][] distance = problem.getDist();
        long startTime = System.currentTimeMillis();
        List<Integer> bestPath = new ArrayList<>();
        try {
            IloCplex cplex = new IloCplex();

            // 设置求解参数
            cplex.setParam(IloCplex.DoubleParam.TiLim, 600); // 设置求解时间限制为 600 秒，可以根据实际情况调整
            // cplex.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Barrier); //
            // 尝试使用不同的根求解算法，这里使用 Barrier 算法
            cplex.setParam(IloCplex.DoubleParam.EpGap, 0.0001); // 设置相对 MIP 间隙为 0，尽量找到最优解

            // 创建决策变量
            IloIntVar[][] intVars = new IloIntVar[cityNum][cityNum];
            for (int i = 0; i < cityNum; i++) {
                for (int j = 0; j < cityNum; j++) {
                    if (i != j) {
                        intVars[i][j] = cplex.intVar(0, 1);
                    }
                }
            }

            // 目标函数
            IloLinearNumExpr target = cplex.linearNumExpr();
            for (int i = 0; i < cityNum; i++) {
                for (int j = 0; j < cityNum; j++) {
                    if (i != j) {
                        target.addTerm(distance[i][j], intVars[i][j]);
                    }
                }
            }
            // 求目标函数的最小值
            cplex.addMinimize(target);
            // 约束
            // 约束1：每行每列之和等于1
            for (int i = 0; i < cityNum; i++) {
                IloLinearNumExpr expr_row = cplex.linearNumExpr();
                IloLinearNumExpr expr_col = cplex.linearNumExpr();
                for (int j = 0; j < cityNum; j++) {
                    if (i != j) {
                        expr_row.addTerm(1, intVars[i][j]);
                        expr_col.addTerm(1, intVars[j][i]);
                    }
                }
                cplex.addEq(expr_row, 1);
                cplex.addEq(expr_col, 1);
            }
            // 约束2：消除子回路
            IloNumVar[] u = cplex.numVarArray(cityNum, 0, Double.MAX_VALUE);
            for (int i = 1; i < cityNum; i++) {
                for (int j = 1; j < cityNum; j++) {
                    if (j != i) {
                        IloLinearNumExpr expr = cplex.linearNumExpr();
                        expr.addTerm(1.0, u[i]);
                        expr.addTerm(-1.0, u[j]);
                        expr.addTerm(cityNum - 1, intVars[i][j]);
                        cplex.addLe(expr, cityNum - 2);
                    }
                }
            }
            // 取消cplex输出
            cplex.setOut(null);
            // 求解
            if (cplex.solve()) {
                bestPath.add(0);
                int index = 0;
                while (true) {
                    for (int i = 0; i < intVars[index].length; i++) {
                        if (index != i && cplex.getValue(intVars[index][i]) > 1e-06) {
                            index = i;
                            bestPath.add(i);
                            break;
                        }
                    }
                    if (index == 0) {
                        break;
                    }
                }
                // System.out.println("最短路径为：" + bestPath);
                // System.out.println("最短路径长度为：" + cplex.getObjValue());

                long endTime = System.currentTimeMillis();

                return new TspPlan(bestPath.stream().mapToInt(Integer::intValue).toArray(),
                        (int) cplex.getBestObjValue(), (endTime - startTime) / 1000.0);
            } else {
                // System.err.println("此题无解");
                return new TspPlan(new int[] { 0 }, -1, cplex.getCplexTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
