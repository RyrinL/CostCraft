package com.costcraft.service;

import com.costcraft.dao.BomItemDao;
import com.costcraft.dao.OverheadConfigDao;
import com.costcraft.dao.ProductDao;
import com.costcraft.dto.BatchContext;
import com.costcraft.dto.UnitCostBreakdown;
import com.costcraft.entity.BomItem;
import com.costcraft.entity.OverheadConfig;
import com.costcraft.entity.Product;
import com.costcraft.exception.ServiceException;
import com.costcraft.service.cost.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UnitCostService {

    private final ProductDao productDao = new ProductDao();
    private final BomItemDao bomItemDao = new BomItemDao();
    private final OverheadConfigDao overheadConfigDao = new OverheadConfigDao();

    public UnitCostBreakdown compute(Connection conn, Long productId) {
        try {
            Product product = productDao.findById(conn, productId);
            if (product == null) throw new ServiceException("Product not found: " + productId);

            List<BomItem> bomItems = bomItemDao.findByProduct(conn, productId);
            BigDecimal multiplier = BigDecimal.ONE;
            BatchContext ctx = new BatchContext(
                    BigDecimal.ZERO,
                    product.getYieldPerBatch(),
                    product.getDaysPerBatch(),
                    multiplier
            );

            // Build decoration chain
            UnitCostCalculation calc = new DirectMaterialsCost(bomItems);

            // Stage 2: DIRECT_OTHER
            List<OverheadConfig> stage2 = overheadConfigDao.findActiveByCostStage(conn, "DIRECT_OTHER");
            for (OverheadConfig cfg : stage2) {
                calc = new DirectOtherCostDecorator(calc, cfg, ctx);
            }

            // Rebuild context with actual totalDirect for Stage 3
            BigDecimal totalDirect = calc.getTotalDirect();
            BatchContext ctxWithDirect = new BatchContext(totalDirect, product.getYieldPerBatch(),
                    product.getDaysPerBatch(), multiplier);

            // Stage 3: PERCENTAGE_OVERHEAD
            List<OverheadConfig> stage3 = overheadConfigDao.findActiveByCostStage(conn, "PERCENTAGE_OVERHEAD");
            for (OverheadConfig cfg : stage3) {
                calc = new PercentageOverheadDecorator(calc, cfg, ctxWithDirect);
            }

            // Stage 4: finalize to per-unit
            calc = new UnitCostFinalizer(calc, product.getYieldPerBatch());

            UnitCostBreakdown result = new UnitCostBreakdown();
            result.productId = productId;
            result.productName = product.getName();
            result.yieldPerBatch = product.getYieldPerBatch();
            result.stage1Materials = sumStage(calc, "STAGE_1");
            result.stage2OtherDirect = sumStage(calc, "STAGE_2");
            result.totalDirect = totalDirect;
            result.stage3Overhead = calc.getTotalOverhead();
            result.totalCost = totalDirect.add(calc.getTotalOverhead());
            result.unitCost = calc.getUnitCost();

            if (product.getYieldPerBatch() != null && product.getYieldPerBatch().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal marginLower = product.getMarginLowerPct() != null ? product.getMarginLowerPct() : new BigDecimal("0.30");
                BigDecimal marginUpper = product.getMarginUpperPct() != null ? product.getMarginUpperPct() : new BigDecimal("0.50");
                result.priceLower = result.unitCost.multiply(BigDecimal.ONE.add(marginLower)).setScale(2, RoundingMode.HALF_UP);
                result.priceUpper = result.unitCost.multiply(BigDecimal.ONE.add(marginUpper)).setScale(2, RoundingMode.HALF_UP);
            }

            result.breakdown = calc.getBreakdown();
            return result;
        } catch (SQLException e) {
            throw new ServiceException("Failed to compute unit cost for product " + productId, e);
        }
    }

    private BigDecimal sumStage(UnitCostCalculation calc, String stage) {
        return calc.getBreakdown().stream()
                .filter(l -> stage.equals(l.getStage()))
                .map(l -> l.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
