package com.costcraft.dao;

import com.costcraft.entity.Material;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDao {

    private static final java.util.Map<String, String> SORT_COLS = java.util.Map.ofEntries(
        java.util.Map.entry("name_asc",       "m.name ASC"),
        java.util.Map.entry("name_desc",      "m.name DESC"),
        java.util.Map.entry("category_asc",   "mc.name ASC"),
        java.util.Map.entry("category_desc",  "mc.name DESC"),
        java.util.Map.entry("pkg_size_asc",   "m.package_size ASC"),
        java.util.Map.entry("pkg_size_desc",  "m.package_size DESC"),
        java.util.Map.entry("pkg_price_asc",  "m.package_price ASC"),
        java.util.Map.entry("pkg_price_desc", "m.package_price DESC"),
        java.util.Map.entry("unit_cost_asc",  "m.unit_cost ASC"),
        java.util.Map.entry("unit_cost_desc", "m.unit_cost DESC"),
        java.util.Map.entry("stock_asc",      "m.current_stock ASC"),
        java.util.Map.entry("stock_desc",     "m.current_stock DESC")
    );

    public List<Material> findAll(Connection conn) throws SQLException {
        return findAll(conn, null, null, null, null);
    }

    public List<Material> findAll(Connection conn, String search, String sort, String status, Long categoryId) throws SQLException {
        String orderBy = sort != null ? SORT_COLS.getOrDefault(sort, "mc.display_order, m.name") : "mc.display_order, m.name";
        boolean hasSearch   = search != null && !search.isBlank();
        boolean filterLow   = "low".equals(status);
        boolean filterOk    = "ok".equals(status);
        boolean filterCat   = categoryId != null;

        StringBuilder where = new StringBuilder();
        if (hasSearch)  where.append("m.name LIKE ? ");
        if (filterCat)  where.append(where.isEmpty() ? "" : "AND ").append("m.category_id = ? ");
        if (filterLow)  where.append(where.isEmpty() ? "" : "AND ").append("m.current_stock <= m.low_stock_threshold ");
        if (filterOk)   where.append(where.isEmpty() ? "" : "AND ").append("m.current_stock > m.low_stock_threshold ");

        String sql = "SELECT m.*, mc.name AS category_name FROM material m "
                + "JOIN material_category mc ON m.category_id = mc.id "
                + (where.isEmpty() ? "" : "WHERE " + where)
                + "ORDER BY " + orderBy;

        List<Material> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if (hasSearch) ps.setString(idx++, "%" + search + "%");
            if (filterCat) ps.setLong(idx++, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Material findById(Connection conn, Long id) throws SQLException {
        String sql = """
            SELECT m.*, mc.name AS category_name
            FROM material m
            JOIN material_category mc ON m.category_id = mc.id
            WHERE m.id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Material> findBelowThreshold(Connection conn) throws SQLException {
        String sql = """
            SELECT m.*, mc.name AS category_name
            FROM material m
            JOIN material_category mc ON m.category_id = mc.id
            WHERE m.current_stock <= m.low_stock_threshold
            ORDER BY m.name
            """;
        List<Material> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Long insert(Connection conn, Material m) throws SQLException {
        String sql = """
            INSERT INTO material
              (category_id, name, package_size, package_price, unit_cost, unit,
               current_stock, low_stock_threshold)
            VALUES (?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, m.getCategoryId());
            ps.setString(2, m.getName());
            ps.setBigDecimal(3, m.getPackageSize());
            ps.setBigDecimal(4, m.getPackagePrice());
            ps.setBigDecimal(5, m.getUnitCost());
            ps.setString(6, m.getUnit());
            ps.setBigDecimal(7, m.getCurrentStock() != null ? m.getCurrentStock() : BigDecimal.ZERO);
            ps.setBigDecimal(8, m.getLowStockThreshold() != null ? m.getLowStockThreshold() : BigDecimal.ZERO);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void update(Connection conn, Material m) throws SQLException {
        String sql = """
            UPDATE material SET
              category_id=?, name=?, package_size=?, package_price=?,
              unit_cost=?, unit=?, low_stock_threshold=?
            WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, m.getCategoryId());
            ps.setString(2, m.getName());
            ps.setBigDecimal(3, m.getPackageSize());
            ps.setBigDecimal(4, m.getPackagePrice());
            ps.setBigDecimal(5, m.getUnitCost());
            ps.setString(6, m.getUnit());
            ps.setBigDecimal(7, m.getLowStockThreshold());
            ps.setLong(8, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(Connection conn, Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM material WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void updateUnitCost(Connection conn, Long id, BigDecimal unitCost) throws SQLException {
        String sql = "UPDATE material SET unit_cost=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, unitCost);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void addStock(Connection conn, Long id, BigDecimal qty) throws SQLException {
        String sql = "UPDATE material SET current_stock = current_stock + ? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, qty);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deductStock(Connection conn, Long id, BigDecimal qty) throws SQLException {
        String sql = "UPDATE material SET current_stock = current_stock - ? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, qty);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public BigDecimal getUnitCost(Connection conn, Long id) throws SQLException {
        String sql = "SELECT unit_cost FROM material WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("unit_cost") : BigDecimal.ZERO;
            }
        }
    }

    public boolean isBelowThreshold(Connection conn, Long id) throws SQLException {
        String sql = "SELECT current_stock <= low_stock_threshold AS below FROM material WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("below");
            }
        }
    }

    private Material mapRow(ResultSet rs) throws SQLException {
        Material m = new Material();
        m.setId(rs.getLong("id"));
        m.setCategoryId(rs.getLong("category_id"));
        m.setCategoryName(rs.getString("category_name"));
        m.setName(rs.getString("name"));
        m.setPackageSize(rs.getBigDecimal("package_size"));
        m.setPackagePrice(rs.getBigDecimal("package_price"));
        m.setUnitCost(rs.getBigDecimal("unit_cost"));
        m.setUnit(rs.getString("unit"));
        m.setCurrentStock(rs.getBigDecimal("current_stock"));
        m.setLowStockThreshold(rs.getBigDecimal("low_stock_threshold"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) m.setCreatedAt(ts.toLocalDateTime());
        return m;
    }
}
