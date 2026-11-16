package com.costcraft.dao;

import com.costcraft.entity.SalesOrder;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesOrderDao {

    public Long insert(Connection conn, SalesOrder o) throws SQLException {
        String sql = """
            INSERT INTO sales_order (customer_name, order_date, note, recorded_revenue, recorded_cost, user_id)
            VALUES (?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getCustomerName());
            ps.setDate(2, Date.valueOf(o.getOrderDate()));
            ps.setString(3, o.getNote());
            ps.setBigDecimal(4, o.getRecordedRevenue());
            ps.setBigDecimal(5, o.getRecordedCost());
            ps.setLong(6, o.getUserId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void updateTotals(Connection conn, Long orderId, BigDecimal revenue, BigDecimal cost) throws SQLException {
        String sql = "UPDATE sales_order SET recorded_revenue = ?, recorded_cost = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, revenue);
            ps.setBigDecimal(2, cost);
            ps.setLong(3, orderId);
            ps.executeUpdate();
        }
    }

    public void delete(Connection conn, Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM sales_order WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<SalesOrder> findAll(Connection conn, String dateFrom, String dateTo, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT so.*, u.username AS username,
                   (SELECT COUNT(*) FROM sales_order_item WHERE order_id = so.id) AS item_count
            FROM sales_order so
            JOIN user u ON so.user_id = u.id
            WHERE 1=1
            """);
        List<Object> params = new ArrayList<>();
        if (dateFrom != null && !dateFrom.isBlank()) { sql.append(" AND so.order_date >= ?"); params.add(dateFrom); }
        if (dateTo != null && !dateTo.isBlank())     { sql.append(" AND so.order_date <= ?"); params.add(dateTo); }
        sql.append(" ORDER BY so.order_date DESC, so.id DESC LIMIT ?");
        params.add(limit);

        List<SalesOrder> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer n) ps.setInt(i + 1, n);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public SalesOrder findById(Connection conn, Long id) throws SQLException {
        String sql = """
            SELECT so.*, u.username AS username,
                   (SELECT COUNT(*) FROM sales_order_item WHERE order_id = so.id) AS item_count
            FROM sales_order so
            JOIN user u ON so.user_id = u.id
            WHERE so.id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private SalesOrder mapRow(ResultSet rs) throws SQLException {
        SalesOrder o = new SalesOrder();
        o.setId(rs.getLong("id"));
        o.setCustomerName(rs.getString("customer_name"));
        Date d = rs.getDate("order_date");
        o.setOrderDate(d != null ? d.toLocalDate() : LocalDate.now());
        o.setNote(rs.getString("note"));
        o.setRecordedRevenue(rs.getBigDecimal("recorded_revenue"));
        o.setRecordedCost(rs.getBigDecimal("recorded_cost"));
        o.setUserId(rs.getLong("user_id"));
        o.setUsername(rs.getString("username"));
        o.setItemCount(rs.getInt("item_count"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
