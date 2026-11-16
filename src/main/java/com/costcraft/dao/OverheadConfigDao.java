package com.costcraft.dao;

import com.costcraft.entity.OverheadConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OverheadConfigDao {

    public List<OverheadConfig> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM overhead_config ORDER BY cost_stage, display_order, name";
        List<OverheadConfig> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<OverheadConfig> findActive(Connection conn) throws SQLException {
        String sql = "SELECT * FROM overhead_config WHERE active=TRUE ORDER BY cost_stage, display_order, name";
        List<OverheadConfig> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<OverheadConfig> findActiveByCostStage(Connection conn, String stage) throws SQLException {
        String sql = "SELECT * FROM overhead_config WHERE active=TRUE AND cost_stage=? ORDER BY display_order, name";
        List<OverheadConfig> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stage);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public OverheadConfig findById(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM overhead_config WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Long insert(Connection conn, OverheadConfig cfg) throws SQLException {
        String sql = """
            INSERT INTO overhead_config
              (name, cost_stage, allocation_rule, fixed_amount, daily_amount,
               days_per_month, percentage, active, display_order)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cfg.getName());
            ps.setString(2, cfg.getCostStage());
            ps.setString(3, cfg.getAllocationRule());
            ps.setBigDecimal(4, cfg.getFixedAmount());
            ps.setBigDecimal(5, cfg.getDailyAmount());
            ps.setInt(6, cfg.getDaysPerMonth() > 0 ? cfg.getDaysPerMonth() : 30);
            ps.setBigDecimal(7, cfg.getPercentage());
            ps.setBoolean(8, cfg.isActive());
            ps.setInt(9, cfg.getDisplayOrder());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void delete(Connection conn, Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM overhead_config WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void update(Connection conn, OverheadConfig cfg) throws SQLException {
        String sql = """
            UPDATE overhead_config SET
              name=?, cost_stage=?, allocation_rule=?, fixed_amount=?,
              daily_amount=?, days_per_month=?, percentage=?, active=?, display_order=?
            WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cfg.getName());
            ps.setString(2, cfg.getCostStage());
            ps.setString(3, cfg.getAllocationRule());
            ps.setBigDecimal(4, cfg.getFixedAmount());
            ps.setBigDecimal(5, cfg.getDailyAmount());
            ps.setInt(6, cfg.getDaysPerMonth());
            ps.setBigDecimal(7, cfg.getPercentage());
            ps.setBoolean(8, cfg.isActive());
            ps.setInt(9, cfg.getDisplayOrder());
            ps.setLong(10, cfg.getId());
            ps.executeUpdate();
        }
    }

    private OverheadConfig mapRow(ResultSet rs) throws SQLException {
        OverheadConfig c = new OverheadConfig();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setCostStage(rs.getString("cost_stage"));
        c.setAllocationRule(rs.getString("allocation_rule"));
        c.setFixedAmount(rs.getBigDecimal("fixed_amount"));
        c.setDailyAmount(rs.getBigDecimal("daily_amount"));
        c.setDaysPerMonth(rs.getInt("days_per_month"));
        c.setPercentage(rs.getBigDecimal("percentage"));
        c.setActive(rs.getBoolean("active"));
        c.setDisplayOrder(rs.getInt("display_order"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());
        return c;
    }
}
