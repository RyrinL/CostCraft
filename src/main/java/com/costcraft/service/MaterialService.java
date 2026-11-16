package com.costcraft.service;

import com.costcraft.dao.*;
import com.costcraft.entity.Material;
import com.costcraft.entity.MaterialCategory;
import com.costcraft.entity.Product;
import com.costcraft.exception.ServiceException;
import com.costcraft.exception.ValidationException;
import com.costcraft.util.DBUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MaterialService {

    private final MaterialDao materialDao = new MaterialDao();
    private final MaterialCategoryDao categoryDao = new MaterialCategoryDao();
    private final UnitCostService unitCostService = new UnitCostService();

    public List<MaterialCategory> findAllCategories() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return categoryDao.findAll(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list categories", e);
        }
    }

    public List<Material> findAll() {
        return findAll(null, null, null, null);
    }

    public List<Material> findAll(String search, String sort, String status, Long categoryId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return materialDao.findAll(conn, search, sort, status, categoryId);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list materials", e);
        }
    }

    public Material findById(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return materialDao.findById(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find material", e);
        }
    }

    public List<Material> findBelowThreshold() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return materialDao.findBelowThreshold(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find low-stock materials", e);
        }
    }

    public Long createMaterial(Material m) {
        validate(m);
        recomputeUnitCost(m);
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return materialDao.insert(conn, m);
        } catch (SQLException e) {
            throw new ServiceException("Failed to create material", e);
        }
    }

    public void updatePrice(Long id, BigDecimal packageSize, BigDecimal packagePrice,
                            BigDecimal lowStockThreshold) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                Material m = materialDao.findById(conn, id);
                if (m == null) throw new ServiceException("Material not found: " + id);

                BigDecimal newUnitCost = packagePrice.divide(packageSize, 4, RoundingMode.HALF_UP);
                m.setPackageSize(packageSize);
                m.setPackagePrice(packagePrice);
                m.setUnitCost(newUnitCost);
                if (lowStockThreshold != null) m.setLowStockThreshold(lowStockThreshold);
                materialDao.update(conn, m);

                // Observer: notify all products that use this material
                MaterialPriceSubject subject = new MaterialPriceSubject();
                ProductDao productDao = new ProductDao();
                List<Product> allProducts = productDao.findActive(conn);
                for (Product p : allProducts) {
                    subject.register(new ProductCostObserver(p.getId(), productDao, unitCostService));
                }
                subject.notifyObservers(conn);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e instanceof ServiceException ? (ServiceException) e
                        : new ServiceException("Failed to update material price", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to update material price", e);
        }
    }

    public void updateFull(Long id, String name, Long categoryId, String unit,
                           BigDecimal packageSize, BigDecimal packagePrice,
                           BigDecimal lowStockThreshold) {
        if (name == null || name.isBlank()) throw new ValidationException("Name is required");
        if (categoryId == null) throw new ValidationException("Category is required");
        if (packageSize == null || packageSize.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Package size must be > 0");
        if (packagePrice == null || packagePrice.compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("Package price must be >= 0");
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                Material m = materialDao.findById(conn, id);
                if (m == null) throw new ServiceException("Material not found: " + id);
                m.setName(name);
                m.setCategoryId(categoryId);
                if (unit != null && !unit.isBlank()) m.setUnit(unit);
                BigDecimal newUnitCost = packagePrice.divide(packageSize, 4, java.math.RoundingMode.HALF_UP);
                m.setPackageSize(packageSize);
                m.setPackagePrice(packagePrice);
                m.setUnitCost(newUnitCost);
                if (lowStockThreshold != null) m.setLowStockThreshold(lowStockThreshold);
                materialDao.update(conn, m);

                MaterialPriceSubject subject = new MaterialPriceSubject();
                ProductDao productDao = new ProductDao();
                List<Product> allProducts = productDao.findActive(conn);
                for (Product p : allProducts) {
                    subject.register(new ProductCostObserver(p.getId(), productDao, unitCostService));
                }
                subject.notifyObservers(conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e instanceof ServiceException ? (ServiceException) e
                        : new ServiceException("Failed to update material", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to update material", e);
        }
    }

    public void deleteMaterial(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            materialDao.delete(conn, id);
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new ValidationException("Cannot delete — this material is used in recipes or has inventory/batch history.");
        } catch (SQLException e) {
            throw new ServiceException("Failed to delete material", e);
        }
    }

    public void updateMaterial(Material m) {
        validate(m);
        recomputeUnitCost(m);
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            materialDao.update(conn, m);
        } catch (SQLException e) {
            throw new ServiceException("Failed to update material", e);
        }
    }

    private void validate(Material m) {
        if (m.getName() == null || m.getName().isBlank()) throw new ValidationException("Name is required");
        if (m.getPackageSize() == null || m.getPackageSize().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Package size must be > 0");
        if (m.getPackagePrice() == null || m.getPackagePrice().compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("Package price must be >= 0");
        if (m.getCategoryId() == null) throw new ValidationException("Category is required");
    }

    private void recomputeUnitCost(Material m) {
        if (m.getPackageSize() != null && m.getPackageSize().compareTo(BigDecimal.ZERO) > 0
                && m.getPackagePrice() != null) {
            m.setUnitCost(m.getPackagePrice().divide(m.getPackageSize(), 4, RoundingMode.HALF_UP));
        }
    }
}
