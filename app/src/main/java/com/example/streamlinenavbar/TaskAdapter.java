package com.example.streamlinenavbar;

public class TaskAdapter {
    private String taskId;
    private String sprintTasks;
    private String teamCode;

    public TaskAdapter() {
    }
    public TaskAdapter(String taskId) {
        this.taskId = taskId;
        this.sprintTasks = sprintTasks;
        this.teamCode = teamCode;
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


