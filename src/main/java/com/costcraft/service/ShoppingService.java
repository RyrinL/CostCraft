package com.costcraft.service;

import com.costcraft.dao.BomItemDao;
import com.costcraft.dao.MaterialDao;
import com.costcraft.dto.ShoppingItem;
import com.costcraft.entity.BomItem;
import com.costcraft.entity.Material;
import com.costcraft.exception.ServiceException;
import com.costcraft.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a "what to buy" shopping list from current stock, low-stock thresholds,
 * and (optionally) a planned production run.
 */
public class ShoppingService {

    private final MaterialDao materialDao = new MaterialDao();
    private final BomItemDao bomItemDao = new BomItemDao();

    /**
     * @param plan productId -> number of batches the user plans to bake (may be empty)
     * @return materials that need buying, planned-production items first, then by name
     */
    public List<ShoppingItem> buildList(Map<Long, BigDecimal> plan) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            // 1. Sum the ingredient grams required across all planned batches.
            Map<Long, BigDecimal> needs = new HashMap<>();
            if (plan != null) {
                for (Map.Entry<Long, BigDecimal> e : plan.entrySet()) {
                    BigDecimal batches = e.getValue();
                    if (batches == null || batches.signum() <= 0) continue;
                    for (BomItem bi : bomItemDao.findByProduct(conn, e.getKey())) {
                        BigDecimal add = bi.getGramQuantity().multiply(batches);
                        needs.merge(bi.getMaterialId(), add, BigDecimal::add);
                    }
                }
            }

            // 2. For every material decide whether (and how much) to buy.
            List<ShoppingItem> list = new ArrayList<>();
            for (Material m : materialDao.findAll(conn, null, null, null, null)) {
                BigDecimal have = m.getCurrentStock();
                BigDecimal threshold = m.getLowStockThreshold();
                BigDecimal need = needs.getOrDefault(m.getId(), BigDecimal.ZERO);

                // Buy enough to cover the larger of "what the plan needs" and "stay above threshold".
                BigDecimal target = need.max(threshold);
                BigDecimal toBuy = target.subtract(have);
                if (toBuy.signum() <= 0) continue;

                BigDecimal estCost = toBuy.multiply(m.getUnitCost());
                boolean below = have.compareTo(threshold) <= 0;
                list.add(new ShoppingItem(m.getName(), m.getUnit(), have, threshold, need, toBuy, estCost, below));
            }

            // 3. Planned-production items first, then alphabetical.
            list.sort(Comparator
                    .comparing((ShoppingItem s) -> s.getNeedForPlan().signum() == 0)
                    .thenComparing(ShoppingItem::getMaterialName, String.CASE_INSENSITIVE_ORDER));
            return list;
        } catch (SQLException e) {
            throw new ServiceException("Failed to build shopping list", e);
        }
    }
}
