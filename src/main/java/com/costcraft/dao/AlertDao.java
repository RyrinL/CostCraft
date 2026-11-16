package com.costcraft.dao;

import com.costcraft.entity.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDao {

    public List<Alert> findUnresolved(Connection conn) throws SQLException {
        String sql = """
            SELECT a.*, m.name AS material_name
            FROM alert a
            JOIN material m ON a.material_id = m.id
            WHERE a.resolved = FALSE
            ORDER BY a.created_at DESC
            """;
        List<Alert> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void create(Connection conn, Long materialId, String alertType) throws SQLException {
        // avoid duplicate open alerts for the same material+type
        String check = "SELECT id FROM alert WHERE material_id=? AND alert_type=? AND resolved=FALSE";
        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setLong(1, materialId);
            ps.setString(2, alertType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return; // already open
            }
        }
        String sql = "INSERT INTO alert (material_id, alert_type, message) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, materialId);
            ps.setString(2, alertType);
            ps.setString(3, "Low stock detected for material id=" + materialId);
            ps.executeUpdate();
        }
    }

    public void resolve(Connection conn, Long alertId) throws SQLException {
        String sql = "UPDATE alert SET resolved=TRUE, resolved_at=NOW() WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, alertId);
            ps.executeUpdate();
        }
    }

    public void resolveByMaterial(Connection conn, Long materialId) throws SQLException {
        String sql = "UPDATE alert SET resolved=TRUE, resolved_at=NOW() WHERE material_id=? AND resolved=FALSE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, materialId);
            ps.executeUpdate();
        }
    }

    public int countUnresolved(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM alert WHERE resolved=FALSE";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Alert mapRow(ResultSet rs) throws SQLException {
        Alert a = new Alert();
        a.setId(rs.getLong("id"));
        a.setMaterialId(rs.getLong("material_id"));
        a.setMaterialName(rs.getString("material_name"));
        a.setAlertType(rs.getString("alert_type"));
        a.setMessage(rs.getString("message"));
        a.setResolved(rs.getBoolean("resolved"));
        Timestamp resAt = rs.getTimestamp("resolved_at");
        if (resAt != null) a.setResolvedAt(resAt.toLocalDateTime());
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) a.setCreatedAt(ts.toLocalDateTime());
        return a;
    }
}
