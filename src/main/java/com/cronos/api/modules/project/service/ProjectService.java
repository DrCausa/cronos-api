package com.cronos.api.modules.project.service;

import com.cronos.api.modules.project.model.Project;
import com.cronos.api.modules.project.model.ProjectCreateRequest;
import com.cronos.api.modules.project.model.ProjectResponse;
import com.cronos.api.modules.project.model.ProjectStatus;
import com.cronos.api.modules.project.repository.ProjectRepository;
import com.cronos.api.modules.workspace.model.WorkspaceRole;
import com.cronos.api.modules.workspace.repository.WorkspaceRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;

    public ProjectService(ProjectRepository projectRepository, WorkspaceRepository workspaceRepository) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
    }

    /**
     * Crea un proyecto validando que el usuario sea OWNER o ADMIN del workspace.
     */
    public ProjectResponse createProject(Integer workspaceId, Integer userId, ProjectCreateRequest request) {
        // 1. Autorización Estricta
        WorkspaceRole role = workspaceRepository.getMemberRole(workspaceId, userId);
        
        if (role == null) {
            throw new SecurityException("No perteneces a este espacio de trabajo.");
        }
        if (role == WorkspaceRole.MEMBER) {
            throw new SecurityException("No tienes permisos suficientes para crear proyectos en este espacio.");
        }

        // 2. Validación de datos obligatorios
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio.");
        }

        // 3. Mapeo al modelo
        Project project = new Project();
        project.setWorkspaceId(workspaceId);
        project.setName(request.getName().trim());
        project.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        
        // Asignamos un color por defecto si no lo envían
        project.setColorHex(request.getColorHex() != null ? request.getColorHex() : "#4CAF50"); 
        project.setStatus(ProjectStatus.ACTIVE);

        // 4. Persistencia
        Project savedProject = projectRepository.createProject(project);

        return new ProjectResponse(savedProject);
    }

    /**
     * Lista los proyectos validando que el usuario pertenezca al workspace.
     */
    public List<ProjectResponse> getWorkspaceProjects(Integer workspaceId, Integer userId) {
        // Validación Básica: Solo miembros del workspace pueden ver los proyectos
        WorkspaceRole role = workspaceRepository.getMemberRole(workspaceId, userId);
        if (role == null) {
            throw new SecurityException("Acceso denegado. No eres miembro de este espacio.");
        }

        return projectRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }
}
