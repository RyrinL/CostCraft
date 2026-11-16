package com.costcraft.dao;

import com.costcraft.entity.CurrencyRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyRateDao {

    public List<CurrencyRate> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM currency_rate ORDER BY code";
        List<CurrencyRate> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public CurrencyRate findByCode(Connection conn, String code) throws SQLException {
        String sql = "SELECT * FROM currency_rate WHERE code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private CurrencyRate mapRow(ResultSet rs) throws SQLException {
        CurrencyRate cr = new CurrencyRate();
        cr.setCode(rs.getString("code"));
        cr.setName(rs.getString("name"));
        cr.setRateToBase(rs.getBigDecimal("rate_to_base"));
        cr.setDecimalPlaces(rs.getInt("decimal_places"));
        cr.setSymbol(rs.getString("symbol"));
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) cr.setUpdatedAt(ts.toLocalDateTime());
        return cr;
    }
}
