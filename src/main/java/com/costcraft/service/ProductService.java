package com.costcraft.service;

import com.costcraft.dao.BomItemDao;
import com.costcraft.dao.MaterialDao;
import com.costcraft.dao.ProductDao;
import com.costcraft.dto.ReplenishLine;
import com.costcraft.dto.UnitCostBreakdown;
import com.costcraft.entity.BomItem;
import com.costcraft.entity.Material;
import com.costcraft.entity.Product;
import com.costcraft.exception.ServiceException;
import com.costcraft.exception.ValidationException;
import com.costcraft.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private final ProductDao productDao = new ProductDao();
    private final BomItemDao bomItemDao = new BomItemDao();
    private final MaterialDao materialDao = new MaterialDao();
    private final UnitCostService unitCostService = new UnitCostService();

    public List<Product> findAll() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return productDao.findAll(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list products", e);
        }
    }

    public List<Product> findActive() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return productDao.findActive(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list active products", e);
        }
    }

    public Product findById(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return productDao.findById(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find product", e);
        }
    }

    public Long createProduct(Product p) {
        validate(p);
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return productDao.insert(conn, p);
        } catch (SQLException e) {
            throw new ServiceException("Failed to create product", e);
        }
    }

    public void updateProduct(Product p) {
        validate(p);
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                productDao.update(conn, p);
                refreshCachedCost(conn, p.getId());
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new ServiceException("Failed to update product", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to update product", e);
        }
    }

    public void deleteProduct(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            productDao.delete(conn, id);
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new ValidationException("Cannot delete — this product has production history. Deactivate it instead (Edit → uncheck Active).");
        } catch (SQLException e) {
            throw new ServiceException("Failed to delete product", e);
        }
    }

    public List<BomItem> getBom(Long productId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return bomItemDao.findByProduct(conn, productId);
        } catch (SQLException e) {
            throw new ServiceException("Failed to get BOM", e);
        }
    }

    public void saveBomItem(BomItem bi) {
        if (bi.getGramQuantity() == null || bi.getGramQuantity().signum() <= 0)
            throw new ValidationException("Quantity must be > 0");
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (bi.getId() != null && bi.getId() > 0) {
                    bomItemDao.update(conn, bi);
                } else {
                    bomItemDao.insert(conn, bi);
                }
                refreshCachedCost(conn, bi.getProductId());
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new ServiceException("Failed to save BOM item", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to save BOM item", e);
        }
    }

    public void deleteBomItem(Long bomItemId, Long productId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                bomItemDao.delete(conn, bomItemId);
                refreshCachedCost(conn, productId);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new ServiceException("Failed to delete BOM item", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to delete BOM item", e);
        }
    }

    public List<ReplenishLine> getReplenishLines(Long productId, BigDecimal multiplier) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            List<ReplenishLine> lines = new ArrayList<>();
            for (BomItem bi : bomItemDao.findByProduct(conn, productId)) {
                Material m = materialDao.findById(conn, bi.getMaterialId());
                BigDecimal required = bi.getGramQuantity().multiply(multiplier);
                lines.add(new ReplenishLine(m.getName(), m.getUnit(), required, m.getCurrentStock()));
            }
            return lines;
        } catch (SQLException e) {
            throw new ServiceException("Failed to compute replenish lines", e);
        }
    }

    public UnitCostBreakdown getUnitCostBreakdown(Long productId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return unitCostService.compute(conn, productId);
        } catch (SQLException e) {
            throw new ServiceException("Failed to compute unit cost", e);
        }
    }

    private void refreshCachedCost(Connection conn, Long productId) throws SQLException {
        UnitCostBreakdown bd = unitCostService.compute(conn, productId);
        Product p = productDao.findById(conn, productId);
        if (p != null) {
            p.setCachedTotalDirect(bd.totalDirect);
            p.setCachedUnitCost(bd.unitCost);
            p.setCachedPriceLower(bd.priceLower);
            p.setCachedPriceUpper(bd.priceUpper);
            productDao.updateCachedCosts(conn, p);
        }
    }

    private void validate(Product p) {
        if (p.getName() == null || p.getName().isBlank()) throw new ValidationException("Name is required");
        if (p.getYieldPerBatch() == null || p.getYieldPerBatch().signum() <= 0)
            throw new ValidationException("Yield per batch must be > 0");
    }
}
