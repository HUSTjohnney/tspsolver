package ttc;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    private final double objectiveValue;
    private final List<TaskAssignment> taskAssignments;

    /**
     * 创建一个新的计划
     * 
     * @throws IloException
     */
    public Plan(double objectiveValue, MILPModel2 model) throws IloException {
        this.objectiveValue = objectiveValue;

        List<TaskAssignment> taskAssignments = new ArrayList<>();
        IloIntVar[][] x = model.getX();
        IloNumVar[][] s = model.getS();

        for (int i = 0; i < model.getInputData().getNumTasks(); i++) {
            for (int k = 0; k < model.getInputData().getNumArcs(); k++) {
                if (model.getVariableValue(x[i][k]) == 1) {
                    taskAssignments.add(new TaskAssignment(i, k, model.getVariableValue(s[i][k])));
                }
            }
        }

        this.taskAssignments = taskAssignments;
    }

    /**
     * 创建一个新的方案类
     * 
     * @param objectiveValue  目标值
     * @param taskAssignments 任务分配
     */
    public Plan(double objectiveValue, List<TaskAssignment> taskAssignments) {
        this.objectiveValue = objectiveValue;
        this.taskAssignments = taskAssignments;
    }

    // getters
    public double getObjectiveValue() {
        return objectiveValue;
    }

    public List<TaskAssignment> getTaskAssignments() {
        return this.taskAssignments;
    }
}
