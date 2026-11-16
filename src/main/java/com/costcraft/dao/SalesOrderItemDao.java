package com.costcraft.dao;

import com.costcraft.entity.SalesOrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesOrderItemDao {

    public void insert(Connection conn, SalesOrderItem it) throws SQLException {
        String sql = """
            INSERT INTO sales_order_item
              (order_id, product_id, quantity, unit_price, unit_cost_snapshot, line_revenue, line_cost)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, it.getOrderId());
            ps.setLong(2, it.getProductId());
            ps.setBigDecimal(3, it.getQuantity());
            ps.setBigDecimal(4, it.getUnitPrice());
            ps.setBigDecimal(5, it.getUnitCostSnapshot());
            ps.setBigDecimal(6, it.getLineRevenue());
            ps.setBigDecimal(7, it.getLineCost());
            ps.executeUpdate();
        }
    }

    public List<SalesOrderItem> findByOrder(Connection conn, Long orderId) throws SQLException {
        String sql = """
            SELECT soi.*, p.name AS product_name
            FROM sales_order_item soi
            JOIN product p ON soi.product_id = p.id
            WHERE soi.order_id = ?
            ORDER BY soi.id
            """;
        List<SalesOrderItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Profit by product across all orders (for the simple profit report). */
    public List<SalesOrderItem> profitByProduct(Connection conn, String dateFrom, String dateTo) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT p.id AS product_id, p.name AS product_name,
                   SUM(soi.quantity)      AS quantity,
                   SUM(soi.line_revenue)  AS line_revenue,
                   SUM(soi.line_cost)     AS line_cost
            FROM sales_order_item soi
            JOIN product p     ON soi.product_id = p.id
            JOIN sales_order so ON soi.order_id  = so.id
            WHERE 1=1
            """);
        List<Object> params = new ArrayList<>();
        if (dateFrom != null && !dateFrom.isBlank()) { sql.append(" AND so.order_date >= ?"); params.add(dateFrom); }
        if (dateTo != null && !dateTo.isBlank())     { sql.append(" AND so.order_date <= ?"); params.add(dateTo); }
        sql.append(" GROUP BY p.id, p.name ORDER BY SUM(soi.line_revenue) - SUM(soi.line_cost) DESC");

        List<SalesOrderItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setString(i + 1, (String) params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SalesOrderItem it = new SalesOrderItem();
                    it.setProductId(rs.getLong("product_id"));
                    it.setProductName(rs.getString("product_name"));
                    it.setQuantity(rs.getBigDecimal("quantity"));
                    it.setLineRevenue(rs.getBigDecimal("line_revenue"));
                    it.setLineCost(rs.getBigDecimal("line_cost"));
                    list.add(it);
                }
            }
        }
        return list;
    }

    private SalesOrderItem mapRow(ResultSet rs) throws SQLException {
        SalesOrderItem it = new SalesOrderItem();
        it.setId(rs.getLong("id"));
        it.setOrderId(rs.getLong("order_id"));
        it.setProductId(rs.getLong("product_id"));
        it.setProductName(rs.getString("product_name"));
        it.setQuantity(rs.getBigDecimal("quantity"));
        it.setUnitPrice(rs.getBigDecimal("unit_price"));
        it.setUnitCostSnapshot(rs.getBigDecimal("unit_cost_snapshot"));
        it.setLineRevenue(rs.getBigDecimal("line_revenue"));
        it.setLineCost(rs.getBigDecimal("line_cost"));
        return it;
    }
}
