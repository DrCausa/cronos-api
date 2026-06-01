package com.cronos.api.modules.task.controller;

import com.cronos.api.modules.task.model.TaskCreateRequest;
import com.cronos.api.modules.task.model.TaskResponse;
import com.cronos.api.modules.task.service.TaskService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * POST /api/workspaces/{workspaceId}/projects/{projectId}/tasks
     */
    public void create(Context ctx) {
        log.info("Recibiendo petición para crear una Tarea...");
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer projectId = Integer.parseInt(ctx.pathParam("projectId"));
            Integer userId = ctx.attribute("userId");
            TaskCreateRequest request = ctx.bodyAsClass(TaskCreateRequest.class);

            TaskResponse response = taskService.createTask(workspaceId, projectId, userId, request);

            ctx.status(201).json(response);
            log.info("Tarea creada exitosamente: ID {}", response.getId());

        } catch (SecurityException e) {
            log.warn("Acceso denegado al crear tarea: {}", e.getMessage());
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(java.util.Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al crear tarea", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error al crear la tarea."));
        }
    }

    /**
     * GET /api/workspaces/{workspaceId}/projects/{projectId}/tasks
     */
    public void getAllByProject(Context ctx) {
        try {
            Integer workspaceId = Integer.parseInt(ctx.pathParam("workspaceId"));
            Integer projectId = Integer.parseInt(ctx.pathParam("projectId"));
            Integer userId = ctx.attribute("userId");

            List<TaskResponse> tasks = taskService.getProjectTasks(workspaceId, projectId, userId);

            ctx.status(200).json(tasks);

        } catch (SecurityException e) {
            ctx.status(403).json(java.util.Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al listar tareas", e);
            ctx.status(500).json(java.util.Map.of("error", "Internal Server Error", "message", "Error al obtener tareas."));
        }
    }
}
