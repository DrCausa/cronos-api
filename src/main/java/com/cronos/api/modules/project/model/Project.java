package com.cronos.api.modules.project.model;

import java.time.LocalDateTime;

public class Project {
    private Integer id;
    private Integer workspaceId;
    private String name;
    private String description;
    private String colorHex;
    private ProjectStatus status;
    private LocalDateTime createdAt;

    public Project() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Integer workspaceId) { this.workspaceId = workspaceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
