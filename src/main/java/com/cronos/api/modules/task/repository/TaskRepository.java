package com.cronos.api.modules.task.repository;

import com.cronos.api.core.DatabaseManager;
import com.cronos.api.modules.task.model.Task;
import com.cronos.api.modules.task.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    private static final Logger log = LoggerFactory.getLogger(TaskRepository.class);
    private final DatabaseManager dbManager;

    public TaskRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Task createTask(Task task) {
        String sql = "INSERT INTO `task` (project_id, name, status) VALUES (?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, task.getProjectId());
            stmt.setString(2, task.getName());
            stmt.setString(3, task.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Fallo al crear la tarea, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                    task.setCreatedAt(java.time.LocalDateTime.now());
                    task.setUpdatedAt(task.getCreatedAt());
                } else {
                    throw new SQLException("Fallo al crear la tarea, no se obtuvo el ID.");
                }
            }
            return task;

        } catch (SQLException e) {
            log.error("Error de BD al crear Tarea", e);
            throw new RuntimeException("Error interno al guardar la tarea.", e);
        }
    }

    public List<Task> findByProjectId(Integer projectId) {
        String sql = "SELECT * FROM `task` WHERE project_id = ? ORDER BY created_at DESC";
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task t = new Task();
                    t.setId(rs.getInt("id"));
                    t.setProjectId(rs.getInt("project_id"));
                    t.setName(rs.getString("name"));
                    t.setStatus(TaskStatus.valueOf(rs.getString("status")));
                    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    tasks.add(t);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar tareas del proyecto: {}", projectId, e);
            throw new RuntimeException("Error de base de datos al listar tareas.", e);
        }
        return tasks;
    }
}
