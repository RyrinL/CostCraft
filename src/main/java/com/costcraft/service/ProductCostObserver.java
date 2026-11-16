package com.costcraft.service;

import com.costcraft.dao.BomItemDao;
import com.costcraft.dao.OverheadConfigDao;
import com.costcraft.dao.ProductDao;
import com.costcraft.dto.UnitCostBreakdown;
import com.costcraft.entity.Product;
import com.costcraft.service.UnitCostService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductCostObserver implements PriceObserver {

    private final Long productId;
    private final ProductDao productDao;
    private final UnitCostService unitCostService;

    public ProductCostObserver(Long productId, ProductDao productDao, UnitCostService unitCostService) {
        this.productId = productId;
        this.productDao = productDao;
        this.unitCostService = unitCostService;
    }

    @Override
    public void onPriceChanged(Connection conn) {
        try {
            UnitCostBreakdown breakdown = unitCostService.compute(conn, productId);
            Product product = productDao.findById(conn, productId);
            if (product != null) {
                product.setCachedTotalDirect(breakdown.totalDirect);
                product.setCachedUnitCost(breakdown.unitCost);
                product.setCachedPriceLower(breakdown.priceLower);
                product.setCachedPriceUpper(breakdown.priceUpper);
                productDao.updateCachedCosts(conn, product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Observer failed to update product cost for id=" + productId, e);
        }
    }
}
