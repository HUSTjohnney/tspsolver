package ttc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class Visualization extends Application {
    private final InputData inputData;
    private final Plan plan;

    public Visualization(InputData inputData, Plan plan) {
        this.inputData = inputData;
        this.plan = plan;
    }

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        int numTasks = inputData.getNumTasks();
        int numArcs = inputData.getNumArcs();
        int[] ST = inputData.getST();
        int[] ET = inputData.getET();
        int[][] d = inputData.getD();

        // 绘制时间轴（横轴）
        int maxEndTime = 0;
        for (int endTime : ET) {
            if (endTime > maxEndTime) {
                maxEndTime = endTime;
            }
        }
        Line timeAxis = new Line(50, 50 + numArcs * 30, 50 + maxEndTime * 20, 50 + numArcs * 30);
        pane.getChildren().add(timeAxis);
        for (int t = 0; t <= maxEndTime; t++) {
            Line tick = new Line(50 + t * 20, 50 + numArcs * 30 - 5, 50 + t * 20, 50 + numArcs * 30 + 5);
            Text timeLabel = new Text(50 + t * 20 - 5, 50 + numArcs * 30 + 20, String.valueOf(t));
            pane.getChildren().addAll(tick, timeLabel);
        }

        // 绘制纵轴
        Line verticalAxis = new Line(50, 50, 50, 50 + numArcs * 30);
        pane.getChildren().add(verticalAxis);
        for (int k = 0; k < numArcs; k++) {
            Text arcLabel = new Text(10, 65 + k * 30, "Arc " + k);
            pane.getChildren().add(arcLabel);
        }

        // 绘制测控弧段
        for (int k = 0; k < numArcs; k++) {
            Rectangle arcRect = new Rectangle(50 + ST[k] * 20, 50 + k * 30, (ET[k] - ST[k]) * 20, 20);
            arcRect.setFill(Color.LIGHTGRAY);
            arcRect.setStroke(Color.BLACK);
            pane.getChildren().add(arcRect);
        }

        // 绘制任务分配
        List<TaskAssignment> taskAssignments = plan.getTaskAssignments();
        for (TaskAssignment assignment : taskAssignments) {
            int taskId = assignment.getTaskId();
            int arcId = assignment.getArcId();
            double startTime = assignment.getStartTime();
            int duration = d[taskId][arcId];

            Rectangle taskRect = new Rectangle(50 + startTime * 20, 50 + arcId * 30, duration * 20, 20);
            taskRect.setFill(Color.RED);
            taskRect.setStroke(Color.BLACK);
            pane.getChildren().add(taskRect);
            Text taskText = new Text(50 + startTime * 20 + 5, 50 + arcId * 30 + 15, "Task " + taskId);
            pane.getChildren().add(taskText);
        }

        // 显示总成本信息
        Text costText = new Text(10, 50 + numArcs * 30 + 50, "Total Cost: " + Math.round(plan.getObjectiveValue()*100)/100.00);
        pane.getChildren().add(costText);

        Scene scene = new Scene(pane, 600, 50 + numArcs * 30 + 60);
        primaryStage.setTitle("Spacecraft Mission Scheduling Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}