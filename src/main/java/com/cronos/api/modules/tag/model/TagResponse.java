package com.cronos.api.modules.tag.model;

public class TagResponse {
    private Integer id;
    private Integer workspaceId;
    private String name;
    private String colorHex;

    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.workspaceId = tag.getWorkspaceId();
        this.name = tag.getName();
        this.colorHex = tag.getColorHex();
    }

    public Integer getId() { return id; }
    public Integer getWorkspaceId() { return workspaceId; }
    public String getName() { return name; }
    public String getColorHex() { return colorHex; }
}
