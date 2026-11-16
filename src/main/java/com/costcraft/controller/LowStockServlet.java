package com.costcraft.controller;

import com.costcraft.service.AlertService;
import com.costcraft.service.MaterialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/low-stock")
public class LowStockServlet extends HttpServlet {

    private final MaterialService materialService = new MaterialService();
    private final AlertService alertService = new AlertService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("lowStockMaterials", materialService.findBelowThreshold());
        req.setAttribute("alerts", alertService.getUnresolved());
        req.getRequestDispatcher("/WEB-INF/views/inventory/low_stock.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String alertId = req.getParameter("resolveAlertId");
        if (alertId != null) {
            alertService.resolve(Long.parseLong(alertId));
        }
        resp.sendRedirect(req.getContextPath() + "/low-stock");
    }
}
