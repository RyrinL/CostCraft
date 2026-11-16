package com.costcraft.dao;

import com.costcraft.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public User findByUsername(Connection conn, String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ? AND active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public User findById(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<User> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM user ORDER BY username";
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Long insert(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO user (username,password_hash,salt,role,email,display_currency,language,active) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getDisplayCurrency() != null ? user.getDisplayCurrency() : "IDR");
            ps.setString(7, user.getLanguage() != null ? user.getLanguage() : "en");
            ps.setBoolean(8, user.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void update(Connection conn, User user) throws SQLException {
        String sql = "UPDATE user SET username=?,role=?,email=?,display_currency=?,active=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getDisplayCurrency());
            ps.setBoolean(5, user.isActive());
            ps.setLong(6, user.getId());
            ps.executeUpdate();
        }
    }

    public void updateLanguage(Connection conn, Long id, String language) throws SQLException {
        String sql = "UPDATE user SET language=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, language);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void updatePassword(Connection conn, Long id, String hash, String salt) throws SQLException {
        String sql = "UPDATE user SET password_hash=?,salt=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, salt);
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    public void updateDisplayCurrency(Connection conn, Long id, String currencyCode) throws SQLException {
        String sql = "UPDATE user SET display_currency=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currencyCode);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public int countOwners(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE role='OWNER' AND active=TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setSalt(rs.getString("salt"));
        u.setRole(rs.getString("role"));
        u.setEmail(rs.getString("email"));
        u.setDisplayCurrency(rs.getString("display_currency"));
        u.setLanguage(rs.getString("language"));
        u.setActive(rs.getBoolean("active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
