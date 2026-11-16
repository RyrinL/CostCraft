package com.costcraft.service;

import com.costcraft.dao.*;
import com.costcraft.dto.UnitCostBreakdown;
import com.costcraft.entity.BomItem;
import com.costcraft.entity.Product;
import com.costcraft.entity.ProductionBatch;
import com.costcraft.exception.ServiceException;
import com.costcraft.exception.ValidationException;
import com.costcraft.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Facade: hides coordination of BatchDao, BatchItemDao, MaterialDao,
 * InventoryLogDao, AlertDao behind a single recordBatch() call.
 */
public class BatchService {

    private final ProductDao productDao = new ProductDao();
    private final BomItemDao bomItemDao = new BomItemDao();
    private final MaterialDao materialDao = new MaterialDao();
    private final ProductionBatchDao batchDao = new ProductionBatchDao();
    private final BatchItemDao batchItemDao = new BatchItemDao();
    private final InventoryLogDao inventoryLogDao = new InventoryLogDao();
    private final AlertDao alertDao = new AlertDao();
    private final UnitCostService unitCostService = new UnitCostService();

    public long recordBatch(Long productId, BigDecimal multiplier,
                            Map<Long, BigDecimal> perBomItemOverrides,
                            Long workerId, String notes) {
        if (multiplier == null || multiplier.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Multiplier must be > 0");

        Connection conn = null;
        try {
            conn = DBUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            UnitCostBreakdown breakdown = unitCostService.compute(conn, productId);
            Product product = productDao.findById(conn, productId);
            if (product == null) throw new ServiceException("Product not found: " + productId);

            BigDecimal unitsProduced = product.getYieldPerBatch().multiply(multiplier);
            BigDecimal totalCost = breakdown.unitCost.multiply(unitsProduced);

            long batchId = batchDao.insertBatch(conn, productId, multiplier, unitsProduced,
                    breakdown.unitCost, totalCost, workerId, notes);

            List<BomItem> bomItems = bomItemDao.findByProduct(conn, productId);
            for (BomItem bi : bomItems) {
                BigDecimal baseQty = (perBomItemOverrides != null && perBomItemOverrides.containsKey(bi.getId()))
                        ? perBomItemOverrides.get(bi.getId())
                        : bi.getGramQuantity();
                boolean overridden = perBomItemOverrides != null && perBomItemOverrides.containsKey(bi.getId());
                BigDecimal qty = baseQty.multiply(multiplier);
                BigDecimal priceSnap = materialDao.getUnitCost(conn, bi.getMaterialId());

                batchItemDao.insert(conn, batchId, bi.getMaterialId(), qty, priceSnap, overridden);
                materialDao.deductStock(conn, bi.getMaterialId(), qty);
                inventoryLogDao.insert(conn, bi.getMaterialId(), qty.negate(),
                        "BATCH_CONSUMPTION", "production_batch", batchId, "Batch #" + batchId, workerId);

                if (materialDao.isBelowThreshold(conn, bi.getMaterialId())) {
                    alertDao.create(conn, bi.getMaterialId(), "LOW_STOCK");
                }
            }

            conn.commit();
            return batchId;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            if (e instanceof ServiceException) throw (ServiceException) e;
            if (e instanceof ValidationException) throw (ValidationException) e;
            throw new ServiceException("Batch logging failed", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { /* ignore */ }
            }
        }
    }

    public List<ProductionBatch> findAll(int limit) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return batchDao.findAll(conn, limit);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list batches", e);
        }
    }

    public List<ProductionBatch> findFiltered(Long productId, String dateFrom, String dateTo, String worker) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return batchDao.findFiltered(conn, productId, dateFrom, dateTo, worker, 500);
        } catch (SQLException e) {
            throw new ServiceException("Failed to filter batches", e);
        }
    }

    public List<ProductionBatch> findByProductLimited(Long productId, int limit) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return batchDao.findByProductLimited(conn, productId, limit);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list batches for product", e);
        }
    }

    public ProductionBatch findById(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return batchDao.findById(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find batch", e);
        }
    }

    public List<com.costcraft.entity.BatchItem> getBatchItems(Long batchId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return new BatchItemDao().findByBatch(conn, batchId);
        } catch (SQLException e) {
            throw new ServiceException("Failed to get batch items", e);
        }
    }
}
