package com.cronos.api.modules.report.controller;

import com.cronos.api.modules.report.model.ReportFilter;
import com.cronos.api.modules.report.model.ReportResponse;
import com.cronos.api.modules.report.service.ReportService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public void getSummary(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId");

            ReportFilter filter = new ReportFilter();
            
            // Mapeo de Query Params
            if (ctx.queryParam("startDate") != null) filter.setStartDate(LocalDateTime.parse(ctx.queryParam("startDate")));
            if (ctx.queryParam("endDate") != null) filter.setEndDate(LocalDateTime.parse(ctx.queryParam("endDate")));
            if (ctx.queryParam("projectId") != null) filter.setProjectId(Integer.parseInt(ctx.queryParam("projectId")));
            if (ctx.queryParam("userId") != null) filter.setUserId(Integer.parseInt(ctx.queryParam("userId")));

            ReportResponse response = reportService.getWorkspaceReport(workspaceId, userId, filter);
            ctx.status(200).json(response);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al generar reporte", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }
}
