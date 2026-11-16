package com.costcraft.controller;

import com.costcraft.entity.User;
import com.costcraft.service.BatchService;
import com.costcraft.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/unit-cost")
public class UnitCostDetailServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final BatchService batchService = new BatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long productId = Long.parseLong(req.getParameter("productId"));
        User user = (User) req.getSession().getAttribute("user");
        var breakdown = productService.getUnitCostBreakdown(productId);
        req.setAttribute("breakdown", breakdown);
        req.setAttribute("product", productService.findById(productId));
        req.setAttribute("user", user);
        req.setAttribute("trendBatches", batchService.findByProductLimited(productId, 20));
        req.getRequestDispatcher("/WEB-INF/views/cost/unit_cost_detail.jsp").forward(req, resp);
    }
}
