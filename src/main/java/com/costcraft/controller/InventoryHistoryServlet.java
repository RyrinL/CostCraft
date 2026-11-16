package com.costcraft.controller;

import com.costcraft.entity.User;
import com.costcraft.service.InventoryService;
import com.costcraft.service.MaterialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/inventory/history")
public class InventoryHistoryServlet extends HttpServlet {

    private final InventoryService inventoryService = new InventoryService();
    private final MaterialService materialService = new MaterialService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String materialIdStr = req.getParameter("materialId");
        int limit = 200;

        if (materialIdStr != null && !materialIdStr.isBlank()) {
            Long materialId = Long.parseLong(materialIdStr);
            req.setAttribute("logs", inventoryService.getHistory(materialId, limit));
            req.setAttribute("material", materialService.findById(materialId));
        } else {
            req.setAttribute("logs", inventoryService.getAllHistory(limit));
        }
        req.setAttribute("materials", materialService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/inventory/inventory_history.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String action = req.getParameter("action");
        if ("reverse".equals(action)) {
            Long logId = Long.parseLong(req.getParameter("logId"));
            try {
                inventoryService.reverseEntry(logId, user.getId());
                resp.sendRedirect(req.getContextPath() + "/inventory/history?success=reversed");
            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
                req.setAttribute("logs", inventoryService.getAllHistory(200));
                req.setAttribute("materials", materialService.findAll());
                req.getRequestDispatcher("/WEB-INF/views/inventory/inventory_history.jsp").forward(req, resp);
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/inventory/history");
        }
    }
}
