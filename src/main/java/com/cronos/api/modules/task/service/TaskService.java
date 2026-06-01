package com.cronos.api.modules.task.service;

import com.cronos.api.modules.project.model.Project;
import com.cronos.api.modules.project.repository.ProjectRepository;
import com.cronos.api.modules.task.model.Task;
import com.cronos.api.modules.task.model.TaskCreateRequest;
import com.cronos.api.modules.task.model.TaskResponse;
import com.cronos.api.modules.task.model.TaskStatus;
import com.cronos.api.modules.task.repository.TaskRepository;
import com.cronos.api.modules.workspace.model.WorkspaceRole;
import com.cronos.api.modules.workspace.repository.WorkspaceRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, WorkspaceRepository workspaceRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
    }

    /**
     * Helper de validación cruzada para evitar código repetido.
     */
    private void validateAccess(Integer workspaceId, Integer projectId, Integer userId, boolean requiresAdmin) {
        // 1. ¿El proyecto existe?
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("El proyecto indicado no existe.");
        }

        // 2. ¿El proyecto realmente pertenece al workspace de la URL?
        if (!project.getWorkspaceId().equals(workspaceId)) {
            throw new SecurityException("El proyecto no pertenece a este espacio de trabajo.");
        }

        // 3. ¿El usuario tiene permisos en este workspace?
        WorkspaceRole role = workspaceRepository.getMemberRole(workspaceId, userId);
        if (role == null) {
            throw new SecurityException("No perteneces a este espacio de trabajo.");
        }

        // 4. ¿Necesita ser admin/owner para esta acción?
        if (requiresAdmin && role == WorkspaceRole.MEMBER) {
            throw new SecurityException("No tienes permisos suficientes para modificar este proyecto.");
        }
    }

    public TaskResponse createTask(Integer workspaceId, Integer projectId, Integer userId, TaskCreateRequest request) {
        // Validar que exista, coincida y que el usuario sea al menos ADMIN
        validateAccess(workspaceId, projectId, userId, true);

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la tarea es obligatorio.");
        }

        Task task = new Task();
        task.setProjectId(projectId);
        task.setName(request.getName().trim());
        task.setStatus(TaskStatus.OPEN);

        Task savedTask = taskRepository.createTask(task);
        return new TaskResponse(savedTask);
    }

    public List<TaskResponse> getProjectTasks(Integer workspaceId, Integer projectId, Integer userId) {
        // Validar que exista, coincida y que el usuario sea miembro (requiresAdmin = false)
        validateAccess(workspaceId, projectId, userId, false);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
}
