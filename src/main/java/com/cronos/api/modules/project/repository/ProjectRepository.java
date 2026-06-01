package com.cronos.api.modules.project.repository;

import com.cronos.api.core.DatabaseManager;
import com.cronos.api.modules.project.model.Project;
import com.cronos.api.modules.project.model.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {

    private static final Logger log = LoggerFactory.getLogger(ProjectRepository.class);
    private final DatabaseManager dbManager;

    public ProjectRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Inserta un nuevo proyecto en la base de datos.
     */
    public Project createProject(Project project) {
        String sql = "INSERT INTO `project` (workspace_id, name, description, color_hex, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, project.getWorkspaceId());
            stmt.setString(2, project.getName());
            stmt.setString(3, project.getDescription());
            stmt.setString(4, project.getColorHex());
            stmt.setString(5, project.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Fallo al crear el proyecto, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                    project.setCreatedAt(java.time.LocalDateTime.now());
                } else {
                    throw new SQLException("Fallo al crear el proyecto, no se obtuvo el ID.");
                }
            }
            return project;

        } catch (SQLException e) {
            log.error("Error al guardar el Proyecto en la base de datos", e);
            throw new RuntimeException("Error interno al crear el proyecto.", e);
        }
    }

    /**
     * Lista todos los proyectos activos de un Workspace.
     */
    public List<Project> findByWorkspaceId(Integer workspaceId) {
        String sql = "SELECT * FROM `project` WHERE workspace_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC";
        List<Project> projects = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workspaceId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project p = new Project();
                    p.setId(rs.getInt("id"));
                    p.setWorkspaceId(rs.getInt("workspace_id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setColorHex(rs.getString("color_hex"));
                    p.setStatus(ProjectStatus.valueOf(rs.getString("status")));
                    p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    
                    projects.add(p);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar proyectos del workspace ID: {}", workspaceId, e);
            throw new RuntimeException("Error de base de datos al buscar proyectos.", e);
        }

        return projects;
    }
    
    /**
     * Busca un proyecto específico por su ID.
     */
    public Project findById(Integer projectId) {
        String sql = "SELECT * FROM `project` WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Project p = new Project();
                    p.setId(rs.getInt("id"));
                    p.setWorkspaceId(rs.getInt("workspace_id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setColorHex(rs.getString("color_hex"));
                    p.setStatus(ProjectStatus.valueOf(rs.getString("status")));
                    p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return p;
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar el proyecto por ID: {}", projectId, e);
        }
        return null;
    }
}
