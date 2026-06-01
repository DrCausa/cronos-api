package com.cronos.api.modules.report.model;

import java.util.List;

public class ReportResponse {
    private Long totalWorkspaceSeconds;
    private List<ProjectSummary> byProject;
    private List<UserSummary> byUser;

    public ReportResponse(Long totalWorkspaceSeconds, List<ProjectSummary> byProject, List<UserSummary> byUser) {
        this.totalWorkspaceSeconds = totalWorkspaceSeconds != null ? totalWorkspaceSeconds : 0L;
        this.byProject = byProject;
        this.byUser = byUser;
    }

    public Long getTotalWorkspaceSeconds() { return totalWorkspaceSeconds; }
    public List<ProjectSummary> getByProject() { return byProject; }
    public List<UserSummary> getByUser() { return byUser; }
}
