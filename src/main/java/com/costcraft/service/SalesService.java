package com.costcraft.service;

import com.costcraft.dao.ProductDao;
import com.costcraft.dao.SalesOrderDao;
import com.costcraft.dao.SalesOrderItemDao;
import com.costcraft.dto.SalesSummary;
import com.costcraft.entity.Product;
import com.costcraft.entity.SalesOrder;
import com.costcraft.entity.SalesOrderItem;
import com.costcraft.exception.ServiceException;
import com.costcraft.exception.ValidationException;
import com.costcraft.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Facade for the sales/order book. recordOrder() writes the order and all its
 * line items in one transaction, snapshotting each product's unit cost (HPP) so
 * profit stays historically accurate even if costs change later.
 */
public class SalesService {

    private final SalesOrderDao orderDao = new SalesOrderDao();
    private final SalesOrderItemDao itemDao = new SalesOrderItemDao();
    private final ProductDao productDao = new ProductDao();

    public long recordOrder(String customerName, LocalDate orderDate, String note,
                            List<Long> productIds, List<BigDecimal> quantities,
                            List<BigDecimal> unitPrices, Long userId) {
        if (customerName == null || customerName.isBlank())
            throw new ValidationException("Customer / event name is required");
        if (orderDate == null) throw new ValidationException("Order date is required");
        if (productIds == null || productIds.isEmpty())
            throw new ValidationException("Add at least one product line");

        Connection conn = null;
        try {
            conn = DBUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            SalesOrder order = new SalesOrder();
            order.setCustomerName(customerName.trim());
            order.setOrderDate(orderDate);
            order.setNote(note);
            order.setUserId(userId);
            order.setRecordedRevenue(BigDecimal.ZERO);
            order.setRecordedCost(BigDecimal.ZERO);
            long orderId = orderDao.insert(conn, order);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalCost = BigDecimal.ZERO;

            for (int i = 0; i < productIds.size(); i++) {
                Long productId = productIds.get(i);
                BigDecimal qty = quantities.get(i);
                BigDecimal price = unitPrices.get(i);
                if (productId == null || qty == null || qty.signum() <= 0 || price == null || price.signum() < 0)
                    continue; // skip blank/invalid lines

                Product product = productDao.findById(conn, productId);
                if (product == null) throw new ServiceException("Product not found: " + productId);

                BigDecimal costSnap = product.getCachedUnitCost() != null
                        ? product.getCachedUnitCost() : BigDecimal.ZERO;
                BigDecimal lineRevenue = price.multiply(qty);
                BigDecimal lineCost = costSnap.multiply(qty);

                SalesOrderItem it = new SalesOrderItem();
                it.setOrderId(orderId);
                it.setProductId(productId);
                it.setQuantity(qty);
                it.setUnitPrice(price);
                it.setUnitCostSnapshot(costSnap);
                it.setLineRevenue(lineRevenue);
                it.setLineCost(lineCost);
                itemDao.insert(conn, it);

                totalRevenue = totalRevenue.add(lineRevenue);
                totalCost = totalCost.add(lineCost);
            }

            if (totalRevenue.signum() == 0 && totalCost.signum() == 0)
                throw new ValidationException("Add at least one valid product line");

            orderDao.updateTotals(conn, orderId, totalRevenue, totalCost);
            conn.commit();
            return orderId;

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            if (e instanceof ServiceException se) throw se;
            if (e instanceof ValidationException ve) throw ve;
            throw new ServiceException("Failed to record order", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
        }
    }

    public List<SalesOrder> findOrders(String dateFrom, String dateTo) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return orderDao.findAll(conn, dateFrom, dateTo, 500);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list orders", e);
        }
    }

    public SalesOrder findOrder(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return orderDao.findById(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find order", e);
        }
    }

    public List<SalesOrderItem> findItems(Long orderId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return itemDao.findByOrder(conn, orderId);
        } catch (SQLException e) {
            throw new ServiceException("Failed to load order items", e);
        }
    }

    public List<SalesOrderItem> profitByProduct(String dateFrom, String dateTo) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return itemDao.profitByProduct(conn, dateFrom, dateTo);
        } catch (SQLException e) {
            throw new ServiceException("Failed to compute profit by product", e);
        }
    }

    public SalesSummary summary(List<SalesOrder> orders) {
        BigDecimal rev = BigDecimal.ZERO, cost = BigDecimal.ZERO;
        for (SalesOrder o : orders) {
            rev = rev.add(o.getRecordedRevenue());
            cost = cost.add(o.getRecordedCost());
        }
        return new SalesSummary(rev, cost, orders.size());
    }

    public void deleteOrder(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            orderDao.delete(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to delete order", e);
        }
    }
}
