package com.cronos.api.modules.workspace.model;

import java.time.LocalDateTime;

public class WorkspaceResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer ownerId;
    private WorkspaceStatus status;
    private LocalDateTime createdAt;

    public WorkspaceResponse() {}

    public WorkspaceResponse(Workspace workspace) {
        this.id = workspace.getId();
        this.name = workspace.getName();
        this.description = workspace.getDescription();
        this.ownerId = workspace.getOwnerId();
        this.status = workspace.getStatus();
        this.createdAt = workspace.getCreatedAt();
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getOwnerId() { return ownerId; }
    public WorkspaceStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
