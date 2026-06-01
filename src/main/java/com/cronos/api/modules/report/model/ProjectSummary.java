package com.cronos.api.modules.report.model;

public class ProjectSummary {
    private Integer projectId;
    private String projectName;
    private String colorHex;
    private Long totalSeconds;

    public ProjectSummary(Integer projectId, String projectName, String colorHex, Long totalSeconds) {
        this.projectId = projectId;
        this.projectName = projectName != null ? projectName : "Sin Proyecto";
        this.colorHex = colorHex != null ? colorHex : "#808080";
        this.totalSeconds = totalSeconds != null ? totalSeconds : 0L;
    }

    public Integer getProjectId() { return projectId; }
    public String getProjectName() { return projectName; }
    public String getColorHex() { return colorHex; }
    public Long getTotalSeconds() { return totalSeconds; }
}
