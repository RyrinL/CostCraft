package com.costcraft.controller;

import com.costcraft.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/replenish")
public class ReplenishServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("products", productService.findActive());

        String productIdStr  = req.getParameter("productId");
        String multiplierStr = req.getParameter("multiplier");

        if (productIdStr != null && !productIdStr.isBlank()) {
            Long productId = Long.parseLong(productIdStr);
            BigDecimal multiplier = (multiplierStr != null && !multiplierStr.isBlank())
                    ? new BigDecimal(multiplierStr) : BigDecimal.ONE;

            try {
                req.setAttribute("lines",     productService.getReplenishLines(productId, multiplier));
                req.setAttribute("product",   productService.findById(productId));
            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
            }
            req.setAttribute("productId",  productIdStr);
            req.setAttribute("multiplier", multiplierStr != null ? multiplierStr : "1");
        }

        req.getRequestDispatcher("/WEB-INF/views/replenish/replenish.jsp").forward(req, resp);
    }
}
