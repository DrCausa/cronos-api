package com.cronos.api.modules.tag.model;

import java.time.LocalDateTime;

public class Tag {
    private Integer id;
    private Integer workspaceId;
    private String name;
    private String colorHex;
    private LocalDateTime createdAt;

    public Tag() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Integer workspaceId) { this.workspaceId = workspaceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
