package com.costcraft.dao;

import com.costcraft.entity.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public List<Product> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM product ORDER BY name";
        List<Product> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Product> findActive(Connection conn) throws SQLException {
        String sql = "SELECT * FROM product WHERE active=TRUE ORDER BY name";
        List<Product> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Product findById(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM product WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Long insert(Connection conn, Product p) throws SQLException {
        String sql = """
            INSERT INTO product
              (name, yield_per_batch, yield_unit, days_per_batch, margin_lower_pct, margin_upper_pct, active)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getYieldPerBatch());
            ps.setString(3, p.getYieldUnit() != null ? p.getYieldUnit() : "pcs");
            ps.setBigDecimal(4, p.getDaysPerBatch());
            ps.setBigDecimal(5, p.getMarginLowerPct() != null ? p.getMarginLowerPct() : new BigDecimal("0.30"));
            ps.setBigDecimal(6, p.getMarginUpperPct() != null ? p.getMarginUpperPct() : new BigDecimal("0.50"));
            ps.setBoolean(7, p.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void update(Connection conn, Product p) throws SQLException {
        String sql = """
            UPDATE product SET
              name=?, yield_per_batch=?, yield_unit=?, days_per_batch=?,
              margin_lower_pct=?, margin_upper_pct=?, active=?
            WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getYieldPerBatch());
            ps.setString(3, p.getYieldUnit() != null ? p.getYieldUnit() : "pcs");
            ps.setBigDecimal(4, p.getDaysPerBatch());
            ps.setBigDecimal(5, p.getMarginLowerPct());
            ps.setBigDecimal(6, p.getMarginUpperPct());
            ps.setBoolean(7, p.isActive());
            ps.setLong(8, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(Connection conn, Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void updateCachedCosts(Connection conn, Product p) throws SQLException {
        String sql = """
            UPDATE product SET
              cached_total_direct=?, cached_unit_cost=?,
              cached_price_lower=?, cached_price_upper=?
            WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, p.getCachedTotalDirect());
            ps.setBigDecimal(2, p.getCachedUnitCost());
            ps.setBigDecimal(3, p.getCachedPriceLower());
            ps.setBigDecimal(4, p.getCachedPriceUpper());
            ps.setLong(5, p.getId());
            ps.executeUpdate();
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setYieldPerBatch(rs.getBigDecimal("yield_per_batch"));
        p.setYieldUnit(rs.getString("yield_unit"));
        p.setDaysPerBatch(rs.getBigDecimal("days_per_batch"));
        p.setMarginLowerPct(rs.getBigDecimal("margin_lower_pct"));
        p.setMarginUpperPct(rs.getBigDecimal("margin_upper_pct"));
        p.setCachedTotalDirect(rs.getBigDecimal("cached_total_direct"));
        p.setCachedUnitCost(rs.getBigDecimal("cached_unit_cost"));
        p.setCachedPriceLower(rs.getBigDecimal("cached_price_lower"));
        p.setCachedPriceUpper(rs.getBigDecimal("cached_price_upper"));
        p.setActive(rs.getBoolean("active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) p.setCreatedAt(ts.toLocalDateTime());
        return p;
    }
}
