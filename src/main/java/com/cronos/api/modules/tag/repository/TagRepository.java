package com.cronos.api.modules.tag.repository;

import com.cronos.api.core.DatabaseManager;
import com.cronos.api.modules.tag.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    private static final Logger log = LoggerFactory.getLogger(TagRepository.class);
    private final DatabaseManager dbManager;

    public TagRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Tag createTag(Tag tag) {
        String sql = "INSERT INTO `tag` (workspace_id, name, color_hex) VALUES (?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tag.getWorkspaceId());
            stmt.setString(2, tag.getName());
            stmt.setString(3, tag.getColorHex());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Fallo al crear la etiqueta, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tag.setId(generatedKeys.getInt(1));
                    tag.setCreatedAt(java.time.LocalDateTime.now());
                } else {
                    throw new SQLException("Fallo al obtener el ID de la etiqueta.");
                }
            }
            return tag;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Ya existe una etiqueta con ese nombre en este espacio de trabajo.");
        } catch (SQLException e) {
            log.error("Error al crear Etiqueta en BD", e);
            throw new RuntimeException("Error interno al guardar la etiqueta.", e);
        }
    }

    public List<Tag> findByWorkspaceId(Integer workspaceId) {
        String sql = "SELECT * FROM `tag` WHERE workspace_id = ? ORDER BY name ASC";
        List<Tag> tags = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workspaceId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tag t = new Tag();
                    t.setId(rs.getInt("id"));
                    t.setWorkspaceId(rs.getInt("workspace_id"));
                    t.setName(rs.getString("name"));
                    t.setColorHex(rs.getString("color_hex"));
                    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    tags.add(t);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar etiquetas del workspace: {}", workspaceId, e);
            throw new RuntimeException("Error al listar etiquetas.", e);
        }
        return tags;
    }
}
