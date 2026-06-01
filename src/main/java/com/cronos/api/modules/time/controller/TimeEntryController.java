package com.cronos.api.modules.time.controller;

import com.cronos.api.modules.time.model.TimeEntryRequest;
import com.cronos.api.modules.time.model.TimeEntryResponse;
import com.cronos.api.modules.time.model.TimeEntryUpdateRequest;
import com.cronos.api.modules.time.service.TimeEntryService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeEntryController {

    private static final Logger log = LoggerFactory.getLogger(TimeEntryController.class);
    private final TimeEntryService timeEntryService;

    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    public void start(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId");
            TimeEntryRequest request = ctx.bodyAsClass(TimeEntryRequest.class);

            TimeEntryResponse response = timeEntryService.startTimer(workspaceId, userId, request);
            ctx.status(201).json(response);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            ctx.status(400).json(java.util.Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al iniciar cronómetro", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }

    public void stop(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer entryId = Integer.parseInt(ctx.pathParam("id"));
            Integer userId = ctx.attribute("userId");

            TimeEntryResponse response = timeEntryService.stopTimer(workspaceId, userId, entryId);
            ctx.status(200).json(response);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(404).json(java.util.Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al detener cronómetro", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }
    
    /**
     * GET /api/workspaces/{workspaceId}/time-entries
     */
    public void getAll(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId");

            java.util.List<TimeEntryResponse> response = timeEntryService.getWorkspaceEntries(workspaceId, userId);
            ctx.status(200).json(response);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener historial de tiempos", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }
    
    public void createManual(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId");
            com.cronos.api.modules.time.model.TimeEntryManualRequest request = ctx.bodyAsClass(com.cronos.api.modules.time.model.TimeEntryManualRequest.class);

            TimeEntryResponse response = timeEntryService.createManual(workspaceId, userId, request);
            ctx.status(201).json(response);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(java.util.Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al registrar tiempo manual", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }
    
    public void update(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer entryId = Integer.parseInt(ctx.pathParam("id"));
            Integer userId = ctx.attribute("userId");
            TimeEntryUpdateRequest request = ctx.bodyAsClass(TimeEntryUpdateRequest.class);

            TimeEntryResponse response = timeEntryService.updateEntry(workspaceId, userId, entryId, request);
            ctx.status(200).json(response);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(java.util.Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }

    public void delete(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer entryId = Integer.parseInt(ctx.pathParam("id"));
            Integer userId = ctx.attribute("userId");

            timeEntryService.deleteEntry(workspaceId, userId, entryId);
            ctx.status(204).result(""); // 204 No Content

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error interno."));
        }
    }
}
