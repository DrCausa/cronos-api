package com.cronos.api.modules.report.service;

import com.cronos.api.modules.report.model.ReportFilter;
import com.cronos.api.modules.report.model.ReportResponse;
import com.cronos.api.modules.report.repository.ReportRepository;
import com.cronos.api.modules.workspace.model.WorkspaceRole;
import com.cronos.api.modules.workspace.repository.WorkspaceRepository;

public class ReportService {

    private final ReportRepository reportRepository;
    private final WorkspaceRepository workspaceRepository;

    public ReportService(ReportRepository reportRepository, WorkspaceRepository workspaceRepository) {
        this.reportRepository = reportRepository;
        this.workspaceRepository = workspaceRepository;
    }

    public ReportResponse getWorkspaceReport(Integer workspaceId, Integer userId, ReportFilter filter) {
        WorkspaceRole role = workspaceRepository.getMemberRole(workspaceId, userId);
        if (role == null) {
            throw new SecurityException("No perteneces a este espacio de trabajo.");
        }

        // Si es MEMBER, forzamos el filtro para que solo vea sus propios datos
        if (role == WorkspaceRole.MEMBER) {
            filter.setUserId(userId);
        }

        Long totalSeconds = reportRepository.getTotalSeconds(workspaceId, filter);
        var byProject = reportRepository.getSummaryByProject(workspaceId, filter);
        var byUser = reportRepository.getSummaryByUser(workspaceId, filter);

        return new ReportResponse(totalSeconds, byProject, byUser);
    }
}
