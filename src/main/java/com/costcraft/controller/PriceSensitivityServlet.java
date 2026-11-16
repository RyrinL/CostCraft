package com.costcraft.controller;

import com.costcraft.dto.UnitCostBreakdown;
import com.costcraft.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/price-sensitivity")
public class PriceSensitivityServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String productIdStr = req.getParameter("productId");
        req.setAttribute("products", productService.findActive());

        if (productIdStr != null && !productIdStr.isBlank()) {
            Long productId = Long.parseLong(productIdStr);
            UnitCostBreakdown bd = productService.getUnitCostBreakdown(productId);

            // Build sensitivity table: margins 10% to 100% in 10% steps
            Map<String, BigDecimal> sensitivity = new LinkedHashMap<>();
            for (int pct = 10; pct <= 100; pct += 10) {
                BigDecimal margin = BigDecimal.valueOf(pct).divide(BigDecimal.valueOf(100));
                BigDecimal price = bd.unitCost.multiply(BigDecimal.ONE.add(margin))
                        .setScale(0, RoundingMode.HALF_UP);
                sensitivity.put(pct + "%", price);
            }
            req.setAttribute("breakdown", bd);
            req.setAttribute("sensitivity", sensitivity);
            req.setAttribute("selectedProductId", productId);
        }
        req.getRequestDispatcher("/WEB-INF/views/cost/price_sensitivity.jsp").forward(req, resp);
    }
}
