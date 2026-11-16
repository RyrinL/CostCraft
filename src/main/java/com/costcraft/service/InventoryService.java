package com.costcraft.service;

import com.costcraft.dao.AlertDao;
import com.costcraft.dao.InventoryLogDao;
import com.costcraft.dao.MaterialDao;
import com.costcraft.entity.InventoryLog;
import com.costcraft.exception.ServiceException;
import com.costcraft.service.inventory.*;
import com.costcraft.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InventoryService {

    private final MaterialDao materialDao = new MaterialDao();
    private final InventoryLogDao logDao = new InventoryLogDao();
    private final AlertDao alertDao = new AlertDao();

    public void purchase(Long materialId, BigDecimal quantity, Long userId, String note) {
        executeCommand(new PurchaseCommand(materialDao, logDao, materialId, quantity, userId, note),
                materialId);
    }

    public void adjust(Long materialId, BigDecimal delta, Long userId, String note) {
        executeCommand(new AdjustmentCommand(materialDao, logDao, materialId, delta, userId, note),
                materialId);
    }

    public void recordWaste(Long materialId, BigDecimal quantity, Long userId, String note) {
        executeCommand(new WasteCommand(materialDao, logDao, materialId, quantity, userId, note),
                materialId);
    }

    public void setInitialStock(Long materialId, BigDecimal quantity, Long userId) {
        executeCommand(new InitialStockCommand(materialDao, logDao, materialId, quantity, userId),
                materialId);
    }

    private void executeCommand(StockMovementCommand cmd, Long materialId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                cmd.execute(conn);
                // auto-resolve alerts if stock is now above threshold
                if (!materialDao.isBelowThreshold(conn, materialId)) {
                    alertDao.resolveByMaterial(conn, materialId);
                } else {
                    alertDao.create(conn, materialId, "LOW_STOCK");
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new ServiceException("Stock movement failed: " + cmd.describe(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error during stock movement", e);
        }
    }

    public List<InventoryLog> getHistory(Long materialId, int limit) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return logDao.findByMaterial(conn, materialId, limit);
        } catch (SQLException e) {
            throw new ServiceException("Failed to fetch inventory history", e);
        }
    }

    public List<InventoryLog> getAllHistory(int limit) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return logDao.findAll(conn, limit);
        } catch (SQLException e) {
            throw new ServiceException("Failed to fetch inventory history", e);
        }
    }

    public void reverseEntry(Long logId, Long userId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                InventoryLog log = logDao.findById(conn, logId);
                if (log == null) throw new com.costcraft.exception.ValidationException("Log entry not found");
                if ("BATCH_CONSUMPTION".equals(log.getReason()))
                    throw new com.costcraft.exception.ValidationException("Batch consumption cannot be reversed here — use inventory adjustment");
                BigDecimal reversal = log.getChangeQuantity().negate();
                materialDao.addStock(conn, log.getMaterialId(), reversal);
                logDao.insert(conn, log.getMaterialId(), reversal, "ADJUSTMENT",
                        "inventory_log", logId, "Reversal of entry #" + logId, userId);
                if (!materialDao.isBelowThreshold(conn, log.getMaterialId())) {
                    alertDao.resolveByMaterial(conn, log.getMaterialId());
                } else {
                    alertDao.create(conn, log.getMaterialId(), "LOW_STOCK");
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof com.costcraft.exception.ValidationException ve) throw ve;
                throw new ServiceException("Failed to reverse entry", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error during reversal", e);
        }
    }
}
