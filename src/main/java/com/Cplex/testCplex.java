package com.Cplex;

import ilog.concert.*;
import ilog.cplex.*;

public class testCplex {

    public static void main(String[] args) {
        try {
            // 创建 CPLEX 对象
            IloCplex cplex = new IloCplex();

            // 定义决策变量 x 和 y
            IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x");
            IloNumVar y = cplex.numVar(0, Double.MAX_VALUE, "y");

            // 定义目标函数：Maximize 3x + 2y
            IloLinearNumExpr obj = cplex.linearNumExpr();
            obj.addTerm(3, x);
            obj.addTerm(2, y);
            cplex.addMaximize(obj);

            // 定义约束条件
            IloLinearNumExpr expr1 = cplex.linearNumExpr();
            expr1.addTerm(2, x);
            expr1.addTerm(1, y);
            cplex.addLe(expr1, 10); // 2x + y ≤ 10

            IloLinearNumExpr expr2 = cplex.linearNumExpr();
            expr2.addTerm(1, x);
            expr2.addTerm(3, y);
            cplex.addLe(expr2, 12); // x + 3y ≤ 12

            // 求解模型
            if (cplex.solve()) {
                System.out.println("Solution status = " + cplex.getStatus());
                System.out.println("Optimal value = " + cplex.getObjValue());
                System.out.println("x = " + cplex.getValue(x));
                System.out.println("y = " + cplex.getValue(y));
            } else {
                System.out.println("No solution found.");
            }

            // 结束 CPLEX 会话
            cplex.end();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}