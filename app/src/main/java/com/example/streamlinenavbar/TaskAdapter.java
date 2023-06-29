package com.example.streamlinenavbar;

public class TaskAdapter {
    private String taskId;
    private String sprintTasks; // Change the field name to sprintTasks
    private String teamCode;

    public TaskAdapter() {
        // Default constructor required for Firestore
    }

    public TaskAdapter(String sprintTasks, String taskId) {
        this.sprintTasks = sprintTasks;
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSprintTasks() {
        return sprintTasks;
    }

    public void setSprintTasks(String sprintTasks) {
        this.sprintTasks = sprintTasks;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }
}
