package com.costcraft.dao;

import com.costcraft.entity.BatchItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BatchItemDao {

    public void insert(Connection conn, Long batchId, Long materialId,
                       BigDecimal gramQty, BigDecimal unitCostSnapshot,
                       boolean overridden) throws SQLException {
        BigDecimal lineCost = gramQty.multiply(unitCostSnapshot);
        String sql = """
            INSERT INTO batch_item
              (batch_id, material_id, gram_quantity, unit_cost_snapshot, line_cost, overridden)
            VALUES (?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, batchId);
            ps.setLong(2, materialId);
            ps.setBigDecimal(3, gramQty);
            ps.setBigDecimal(4, unitCostSnapshot);
            ps.setBigDecimal(5, lineCost);
            ps.setBoolean(6, overridden);
            ps.executeUpdate();
        }
    }

    public List<BatchItem> findByBatch(Connection conn, Long batchId) throws SQLException {
        String sql = """
            SELECT bi.*, m.name AS material_name
            FROM batch_item bi
            JOIN material m ON bi.material_id = m.id
            WHERE bi.batch_id = ?
            ORDER BY m.name
            """;
        List<BatchItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, batchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private BatchItem mapRow(ResultSet rs) throws SQLException {
        BatchItem bi = new BatchItem();
        bi.setId(rs.getLong("id"));
        bi.setBatchId(rs.getLong("batch_id"));
        bi.setMaterialId(rs.getLong("material_id"));
        bi.setMaterialName(rs.getString("material_name"));
        bi.setGramQuantity(rs.getBigDecimal("gram_quantity"));
        bi.setUnitCostSnapshot(rs.getBigDecimal("unit_cost_snapshot"));
        bi.setLineCost(rs.getBigDecimal("line_cost"));
        bi.setOverridden(rs.getBoolean("overridden"));
        return bi;
    }
}
