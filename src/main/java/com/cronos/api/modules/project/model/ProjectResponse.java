package com.cronos.api.modules.project.model;

public class ProjectResponse {
    private Integer id;
    private String name;
    private String description;
    private String colorHex;
    private ProjectStatus status;

    public ProjectResponse(Project p) {
        this.id = p.getId();
        this.name = p.getName();
        this.description = p.getDescription();
        this.colorHex = p.getColorHex();
        this.status = p.getStatus();
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getColorHex() { return colorHex; }
    public ProjectStatus getStatus() { return status; }
}
