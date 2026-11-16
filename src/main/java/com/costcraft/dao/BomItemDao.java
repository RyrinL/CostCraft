package com.costcraft.dao;

import com.costcraft.entity.BomItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BomItemDao {

    public List<BomItem> findByProduct(Connection conn, Long productId) throws SQLException {
        String sql = """
            SELECT bi.*, m.name AS material_name, m.unit AS material_unit, m.unit_cost AS material_unit_cost
            FROM bom_item bi
            JOIN material m ON bi.material_id = m.id
            WHERE bi.product_id = ?
            ORDER BY bi.display_order, m.name
            """;
        List<BomItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public BomItem findById(Connection conn, Long id) throws SQLException {
        String sql = """
            SELECT bi.*, m.name AS material_name, m.unit AS material_unit, m.unit_cost AS material_unit_cost
            FROM bom_item bi
            JOIN material m ON bi.material_id = m.id
            WHERE bi.id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Long insert(Connection conn, BomItem bi) throws SQLException {
        String sql = """
            INSERT INTO bom_item (product_id, material_id, gram_quantity, display_order)
            VALUES (?,?,?,?)
            ON DUPLICATE KEY UPDATE gram_quantity=VALUES(gram_quantity)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, bi.getProductId());
            ps.setLong(2, bi.getMaterialId());
            ps.setBigDecimal(3, bi.getGramQuantity());
            ps.setInt(4, bi.getDisplayOrder());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                return null;
            }
        }
    }

    public void update(Connection conn, BomItem bi) throws SQLException {
        String sql = "UPDATE bom_item SET gram_quantity=?, display_order=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, bi.getGramQuantity());
            ps.setInt(2, bi.getDisplayOrder());
            ps.setLong(3, bi.getId());
            ps.executeUpdate();
        }
    }

    public void delete(Connection conn, Long id) throws SQLException {
        String sql = "DELETE FROM bom_item WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void deleteByProduct(Connection conn, Long productId) throws SQLException {
        String sql = "DELETE FROM bom_item WHERE product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.executeUpdate();
        }
    }

    private BomItem mapRow(ResultSet rs) throws SQLException {
        BomItem bi = new BomItem();
        bi.setId(rs.getLong("id"));
        bi.setProductId(rs.getLong("product_id"));
        bi.setMaterialId(rs.getLong("material_id"));
        bi.setMaterialName(rs.getString("material_name"));
        bi.setMaterialUnit(rs.getString("material_unit"));
        bi.setGramQuantity(rs.getBigDecimal("gram_quantity"));
        bi.setMaterialUnitCost(rs.getBigDecimal("material_unit_cost"));
        bi.setDisplayOrder(rs.getInt("display_order"));
        return bi;
    }
}
