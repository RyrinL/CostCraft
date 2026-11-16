package com.costcraft.controller;

import com.costcraft.service.AlertService;
import com.costcraft.service.BatchService;
import com.costcraft.service.MaterialService;
import com.costcraft.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final MaterialService materialService = new MaterialService();
    private final AlertService alertService = new AlertService();
    private final BatchService batchService = new BatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("products", productService.findActive());
        req.setAttribute("lowStockMaterials", materialService.findBelowThreshold());
        req.setAttribute("alerts", alertService.getUnresolved());
        req.setAttribute("recentBatches", batchService.findAll(5));
        req.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp").forward(req, resp);
    }
}
