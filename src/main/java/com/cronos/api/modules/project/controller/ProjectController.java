package com.cronos.api.modules.project.controller;

import com.cronos.api.modules.project.model.ProjectCreateRequest;
import com.cronos.api.modules.project.model.ProjectResponse;
import com.cronos.api.modules.project.service.ProjectService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * POST /api/workspaces/{workspaceId}/projects
     */
    public void create(Context ctx) {
        log.info("Recibiendo petición para crear un Proyecto...");

        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId"); // Extraído del token JWT
            ProjectCreateRequest request = ctx.bodyAsClass(ProjectCreateRequest.class);

            ProjectResponse response = projectService.createProject(workspaceId, userId, request);

            ctx.status(201).json(response);
            log.info("Proyecto creado exitosamente: ID {}", response.getId());

        } catch (SecurityException e) {
            log.warn("Intento de creación denegado: {}", e.getMessage());
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(java.util.Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al crear proyecto", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error al crear el proyecto."));
        }
    }

    /**
     * GET /api/workspaces/{workspaceId}/projects
     */
    public void getAllByWorkspace(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer userId = ctx.attribute("userId");

            List<ProjectResponse> projects = projectService.getWorkspaceProjects(workspaceId, userId);

            ctx.status(200).json(projects);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al listar proyectos", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error al obtener proyectos."));
        }
    }
}
