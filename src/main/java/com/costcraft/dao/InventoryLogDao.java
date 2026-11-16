package com.costcraft.dao;

import com.costcraft.entity.InventoryLog;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogDao {

    public void insert(Connection conn, Long materialId, BigDecimal changeQty, String reason,
                       String referenceType, Long referenceId, String note, Long userId) throws SQLException {
        String sql = """
            INSERT INTO inventory_log
              (material_id, change_quantity, reason, reference_type, reference_id, note, user_id)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, materialId);
            ps.setBigDecimal(2, changeQty);
            ps.setString(3, reason);
            ps.setString(4, referenceType);
            if (referenceId != null) ps.setLong(5, referenceId); else ps.setNull(5, Types.BIGINT);
            ps.setString(6, note);
            ps.setLong(7, userId);
            ps.executeUpdate();
        }
    }

    public List<InventoryLog> findByMaterial(Connection conn, Long materialId, int limit) throws SQLException {
        String sql = """
            SELECT il.*, m.name AS material_name, u.username
            FROM inventory_log il
            JOIN material m ON il.material_id = m.id
            JOIN user u ON il.user_id = u.id
            WHERE il.material_id = ?
            ORDER BY il.created_at DESC
            LIMIT ?
            """;
        List<InventoryLog> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, materialId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<InventoryLog> findAll(Connection conn, int limit) throws SQLException {
        String sql = """
            SELECT il.*, m.name AS material_name, u.username
            FROM inventory_log il
            JOIN material m ON il.material_id = m.id
            JOIN user u ON il.user_id = u.id
            ORDER BY il.created_at DESC
            LIMIT ?
            """;
        List<InventoryLog> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public InventoryLog findById(Connection conn, Long id) throws SQLException {
        String sql = """
            SELECT il.*, m.name AS material_name, u.username
            FROM inventory_log il
            JOIN material m ON il.material_id = m.id
            JOIN user u ON il.user_id = u.id
            WHERE il.id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private InventoryLog mapRow(ResultSet rs) throws SQLException {
        InventoryLog log = new InventoryLog();
        log.setId(rs.getLong("id"));
        log.setMaterialId(rs.getLong("material_id"));
        log.setMaterialName(rs.getString("material_name"));
        log.setChangeQuantity(rs.getBigDecimal("change_quantity"));
        log.setReason(rs.getString("reason"));
        log.setReferenceType(rs.getString("reference_type"));
        long refId = rs.getLong("reference_id");
        if (!rs.wasNull()) log.setReferenceId(refId);
        log.setNote(rs.getString("note"));
        log.setUserId(rs.getLong("user_id"));
        log.setUsername(rs.getString("username"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
