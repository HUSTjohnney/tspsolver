package ttc;

import ilog.concert.IloException;
import javafx.application.Application;
import javafx.stage.Stage;
import ttc.TaskAssignment;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 示例输入数据
        int numTasks = 3;
        int numArcs = 4;
        double[] phi = { 1.0, 2.0, 3.0, 4.0 };
        double[] delta = { 0.5, 0.6, 0.7 };

        // 任务在测控弧段上的测控时间，100 表示不可传输
        int[][] d = {
                { 2, 3, 100, 100 },
                { 100, 2, 3, 100 },
                { 100, 100, 2, 3 }
        };
        int[] o = { 1, 1, 1 };
        int[] ST = { 0, 2, 4, 6 };
        int[] ET = { 5, 7, 9, 11 };

        InputData inputData = new InputData(numTasks, numArcs, phi, delta, d, o, ST, ET);
        MILPModel2 model = new MILPModel2(inputData);

        try {
            model.buildModel();
            if (model.solve()) {
                System.out.println("Objective value: " + model.getObjectiveValue());

                // 创建 Plan 对象
                Plan plan = new Plan(model.getObjectiveValue(), model);

                // 输出任务分配信息
                for (TaskAssignment assignment : plan.getTaskAssignments()) {
                    System.out.println("Task " + assignment.getTaskId() + " is assigned to Arc " + assignment.getArcId()
                            + " at time " + assignment.getStartTime());
                }

                // 输出弧段的开始时刻和结束时刻
                for (int k = 0; k < numArcs; k++) {
                    System.out.println(
                            "Arc " + k + " starts at " + inputData.getST()[k] + " and ends at " + inputData.getET()[k]);
                }

                // 启动可视化界面
                Visualization visualization = new Visualization(inputData, plan);
                visualization.start(new Stage());
            } else {
                System.out.println("No solution found.");
            }
            model.end();
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}