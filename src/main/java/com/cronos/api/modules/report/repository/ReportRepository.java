package com.cronos.api.modules.report.repository;

import com.cronos.api.core.DatabaseManager;
import com.cronos.api.modules.report.model.ProjectSummary;
import com.cronos.api.modules.report.model.ReportFilter;
import com.cronos.api.modules.report.model.UserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportRepository {

    private static final Logger log = LoggerFactory.getLogger(ReportRepository.class);
    private final DatabaseManager dbManager;

    public ReportRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    private String buildBaseQuery(ReportFilter filter) {
        StringBuilder sql = new StringBuilder("FROM `time_entry` t WHERE t.workspace_id = ? AND t.end_time IS NOT NULL");
        if (filter.getStartDate() != null) sql.append(" AND t.start_time >= ?");
        if (filter.getEndDate() != null) sql.append(" AND t.start_time <= ?");
        if (filter.getProjectId() != null) sql.append(" AND t.project_id = ?");
        if (filter.getUserId() != null) sql.append(" AND t.user_id = ?");
        return sql.toString();
    }

    private void setFilterParams(PreparedStatement stmt, Integer workspaceId, ReportFilter filter) throws SQLException {
        int index = 1;
        stmt.setInt(index++, workspaceId);
        if (filter.getStartDate() != null) stmt.setTimestamp(index++, Timestamp.valueOf(filter.getStartDate()));
        if (filter.getEndDate() != null) stmt.setTimestamp(index++, Timestamp.valueOf(filter.getEndDate()));
        if (filter.getProjectId() != null) stmt.setInt(index++, filter.getProjectId());
        if (filter.getUserId() != null) stmt.setInt(index++, filter.getUserId());
    }

    public Long getTotalSeconds(Integer workspaceId, ReportFilter filter) {
        String sql = "SELECT SUM(TIMESTAMPDIFF(SECOND, t.start_time, t.end_time)) " + buildBaseQuery(filter);
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setFilterParams(stmt, workspaceId, filter);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) { log.error("Error al calcular total", e); }
        return 0L;
    }

    public List<ProjectSummary> getSummaryByProject(Integer workspaceId, ReportFilter filter) {
        String sql = "SELECT p.id, p.name, p.color_hex, SUM(TIMESTAMPDIFF(SECOND, t.start_time, t.end_time)) as total " +
                     buildBaseQuery(filter).replace("FROM `time_entry` t", "FROM `time_entry` t LEFT JOIN `project` p ON t.project_id = p.id") +
                     " GROUP BY p.id, p.name, p.color_hex ORDER BY total DESC";
                     
        List<ProjectSummary> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setFilterParams(stmt, workspaceId, filter);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProjectSummary(rs.getObject("id", Integer.class), rs.getString("name"), rs.getString("color_hex"), rs.getLong("total")));
                }
            }
        } catch (SQLException e) { log.error("Error agrupando por proyecto", e); }
        return list;
    }

    public List<UserSummary> getSummaryByUser(Integer workspaceId, ReportFilter filter) {
        String sql = "SELECT u.id, u.username, SUM(TIMESTAMPDIFF(SECOND, t.start_time, t.end_time)) as total " +
                     buildBaseQuery(filter).replace("FROM `time_entry` t", "FROM `time_entry` t JOIN `user` u ON t.user_id = u.id") +
                     " GROUP BY u.id, u.username ORDER BY total DESC";
                     
        List<UserSummary> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setFilterParams(stmt, workspaceId, filter);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new UserSummary(rs.getInt("id"), rs.getString("username"), rs.getLong("total")));
                }
            }
        } catch (SQLException e) { log.error("Error agrupando por usuario", e); }
        return list;
    }
}
