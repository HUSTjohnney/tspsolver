package ttc;

public class TaskAssignment {
    private final int taskId;
    private final int arcId;
    private final double startTime;

    public TaskAssignment(int taskId, int arcId, double startTime) {
        this.taskId = taskId;
        this.arcId = arcId;
        this.startTime = startTime;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getArcId() {
        return arcId;
    }

    public double getStartTime() {
        return startTime;
    }
}
