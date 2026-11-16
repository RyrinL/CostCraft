package com.costcraft.controller;

import com.costcraft.entity.CurrencyRate;
import com.costcraft.entity.SalesOrder;
import com.costcraft.entity.User;
import com.costcraft.service.CurrencyService;
import com.costcraft.service.ProductService;
import com.costcraft.service.SalesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Sales / order book. Owner-only (enforced by RoleFilter on /sales). */
@WebServlet("/sales")
public class SalesServlet extends HttpServlet {

    private final SalesService salesService = new SalesService();
    private final ProductService productService = new ProductService();
    private final CurrencyService currencyService = new CurrencyService();

    /** Expose the user's display-currency params for the form's live profit preview. */
    private void setCurrency(HttpServletRequest req) {
        User user = (req.getSession(false) != null) ? (User) req.getSession(false).getAttribute("user") : null;
        String code = (user != null && user.getDisplayCurrency() != null) ? user.getDisplayCurrency() : "IDR";
        CurrencyRate rate = currencyService.findByCode(code);
        req.setAttribute("curSymbol", rate != null ? rate.getSymbol() : "Rp ");
        req.setAttribute("curRate", rate != null ? rate.getRateToBase() : BigDecimal.ONE);
        req.setAttribute("curDecimals", rate != null ? rate.getDecimalPlaces() : 0);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("new".equals(action)) {
            req.setAttribute("products", productService.findActive());
            req.setAttribute("today", LocalDate.now().toString());
            setCurrency(req);
            req.getRequestDispatcher("/WEB-INF/views/sales/sales_form.jsp").forward(req, resp);
            return;
        }
        if ("view".equals(action)) {
            Long id = Long.parseLong(req.getParameter("id"));
            req.setAttribute("order", salesService.findOrder(id));
            req.setAttribute("items", salesService.findItems(id));
            req.getRequestDispatcher("/WEB-INF/views/sales/sales_detail.jsp").forward(req, resp);
            return;
        }

        // default: order list + summary + profit-by-product
        String from = req.getParameter("from");
        String to   = req.getParameter("to");
        List<SalesOrder> orders = salesService.findOrders(from, to);
        req.setAttribute("orders", orders);
        req.setAttribute("summary", salesService.summary(orders));
        req.setAttribute("byProduct", salesService.profitByProduct(from, to));
        req.setAttribute("from", from != null ? from : "");
        req.setAttribute("to", to != null ? to : "");
        req.getRequestDispatcher("/WEB-INF/views/sales/sales_list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            salesService.deleteOrder(Long.parseLong(req.getParameter("id")));
            resp.sendRedirect(req.getContextPath() + "/sales?success=deleted");
            return;
        }

        // create
        try {
            User user = (User) req.getSession().getAttribute("user");
            String customer = req.getParameter("customerName");
            LocalDate date = parseDate(req.getParameter("orderDate"));
            String note = req.getParameter("note");

            String[] productIds = req.getParameterValues("productId");
            String[] quantities = req.getParameterValues("quantity");
            String[] prices     = req.getParameterValues("unitPrice");

            List<Long> pIds = new ArrayList<>();
            List<BigDecimal> qtys = new ArrayList<>();
            List<BigDecimal> uPrices = new ArrayList<>();
            if (productIds != null) {
                for (int i = 0; i < productIds.length; i++) {
                    if (productIds[i] == null || productIds[i].isBlank()) continue;
                    pIds.add(Long.parseLong(productIds[i]));
                    qtys.add(parseDec(quantities, i));
                    uPrices.add(parseDec(prices, i));
                }
            }

            long id = salesService.recordOrder(customer, date, note, pIds, qtys, uPrices, user.getId());
            resp.sendRedirect(req.getContextPath() + "/sales?action=view&id=" + id);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("products", productService.findActive());
            req.setAttribute("today", LocalDate.now().toString());
            setCurrency(req);
            req.getRequestDispatcher("/WEB-INF/views/sales/sales_form.jsp").forward(req, resp);
        }
    }

    private LocalDate parseDate(String v) {
        try { return (v != null && !v.isBlank()) ? LocalDate.parse(v) : LocalDate.now(); }
        catch (Exception e) { return LocalDate.now(); }
    }

    private BigDecimal parseDec(String[] arr, int i) {
        try {
            String v = (arr != null && i < arr.length) ? arr[i] : null;
            return (v != null && !v.isBlank()) ? new BigDecimal(v.trim()) : BigDecimal.ZERO;
        } catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }
}
