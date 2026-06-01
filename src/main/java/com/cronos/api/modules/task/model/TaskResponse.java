package com.cronos.api.modules.task.model;

public class TaskResponse {
    private Integer id;
    private Integer projectId;
    private String name;
    private TaskStatus status;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.projectId = task.getProjectId();
        this.name = task.getName();
        this.status = task.getStatus();
    }

    public Integer getId() { return id; }
    public Integer getProjectId() { return projectId; }
    public String getName() { return name; }
    public TaskStatus getStatus() { return status; }
}
