package com.costcraft.dao;

import com.costcraft.entity.ProductionBatch;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductionBatchDao {

    public Long insertBatch(Connection conn, Long productId, BigDecimal multiplier,
                            BigDecimal unitsProduced, BigDecimal unitCost, BigDecimal totalCost,
                            Long workerId, String notes) throws SQLException {
        String sql = """
            INSERT INTO production_batch
              (product_id, multiplier, units_produced, recorded_unit_cost, recorded_total_cost, worker_id, notes)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, productId);
            ps.setBigDecimal(2, multiplier);
            ps.setBigDecimal(3, unitsProduced);
            ps.setBigDecimal(4, unitCost);
            ps.setBigDecimal(5, totalCost);
            ps.setLong(6, workerId);
            ps.setString(7, notes);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public List<ProductionBatch> findAll(Connection conn, int limit) throws SQLException {
        String sql = """
            SELECT pb.*, p.name AS product_name, u.username AS worker_username
            FROM production_batch pb
            JOIN product p ON pb.product_id = p.id
            JOIN user u ON pb.worker_id = u.id
            ORDER BY pb.created_at DESC
            LIMIT ?
            """;
        List<ProductionBatch> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<ProductionBatch> findByProduct(Connection conn, Long productId) throws SQLException {
        String sql = """
            SELECT pb.*, p.name AS product_name, u.username AS worker_username
            FROM production_batch pb
            JOIN product p ON pb.product_id = p.id
            JOIN user u ON pb.worker_id = u.id
            WHERE pb.product_id = ?
            ORDER BY pb.created_at DESC
            """;
        List<ProductionBatch> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<ProductionBatch> findByProductLimited(Connection conn, Long productId, int limit) throws SQLException {
        String sql = """
            SELECT pb.*, p.name AS product_name, u.username AS worker_username
            FROM production_batch pb
            JOIN product p ON pb.product_id = p.id
            JOIN user u ON pb.worker_id = u.id
            WHERE pb.product_id = ?
            ORDER BY pb.created_at ASC
            LIMIT ?
            """;
        List<ProductionBatch> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<ProductionBatch> findFiltered(Connection conn, Long productId,
            String dateFrom, String dateTo, String worker, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT pb.*, p.name AS product_name, u.username AS worker_username
            FROM production_batch pb
            JOIN product p ON pb.product_id = p.id
            JOIN user u ON pb.worker_id = u.id
            WHERE 1=1
            """);
        List<Object> params = new ArrayList<>();
        if (productId != null) { sql.append(" AND pb.product_id = ?"); params.add(productId); }
        if (dateFrom != null && !dateFrom.isBlank()) { sql.append(" AND DATE(pb.created_at) >= ?"); params.add(dateFrom); }
        if (dateTo != null && !dateTo.isBlank()) { sql.append(" AND DATE(pb.created_at) <= ?"); params.add(dateTo); }
        if (worker != null && !worker.isBlank()) { sql.append(" AND u.username LIKE ?"); params.add("%" + worker + "%"); }
        sql.append(" ORDER BY pb.created_at DESC LIMIT ?");
        params.add(limit);

        List<ProductionBatch> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Long l) ps.setLong(i + 1, l);
                else if (p instanceof Integer n) ps.setInt(i + 1, n);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public ProductionBatch findById(Connection conn, Long id) throws SQLException {
        String sql = """
            SELECT pb.*, p.name AS product_name, u.username AS worker_username
            FROM production_batch pb
            JOIN product p ON pb.product_id = p.id
            JOIN user u ON pb.worker_id = u.id
            WHERE pb.id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private ProductionBatch mapRow(ResultSet rs) throws SQLException {
        ProductionBatch b = new ProductionBatch();
        b.setId(rs.getLong("id"));
        b.setProductId(rs.getLong("product_id"));
        b.setProductName(rs.getString("product_name"));
        b.setMultiplier(rs.getBigDecimal("multiplier"));
        b.setUnitsProduced(rs.getBigDecimal("units_produced"));
        b.setRecordedUnitCost(rs.getBigDecimal("recorded_unit_cost"));
        b.setRecordedTotalCost(rs.getBigDecimal("recorded_total_cost"));
        b.setWorkerId(rs.getLong("worker_id"));
        b.setWorkerUsername(rs.getString("worker_username"));
        b.setNotes(rs.getString("notes"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        return b;
    }
}
