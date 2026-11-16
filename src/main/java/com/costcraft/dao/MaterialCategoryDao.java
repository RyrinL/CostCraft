package com.costcraft.dao;

import com.costcraft.entity.MaterialCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialCategoryDao {

    public List<MaterialCategory> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM material_category ORDER BY display_order, name";
        List<MaterialCategory> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public MaterialCategory findById(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM material_category WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Long insert(Connection conn, MaterialCategory cat) throws SQLException {
        String sql = "INSERT INTO material_category (name, display_order) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getName());
            ps.setInt(2, cat.getDisplayOrder());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private MaterialCategory mapRow(ResultSet rs) throws SQLException {
        MaterialCategory c = new MaterialCategory();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDisplayOrder(rs.getInt("display_order"));
        return c;
    }
}
