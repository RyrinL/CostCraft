package com.costcraft.controller;

import com.costcraft.entity.User;
import com.costcraft.service.InventoryService;
import com.costcraft.service.MaterialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/inventory")
public class InventoryServlet extends HttpServlet {

    private final InventoryService inventoryService = new InventoryService();
    private final MaterialService materialService = new MaterialService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String search = req.getParameter("search");
        String sort   = req.getParameter("sort");
        req.setAttribute("materials", materialService.findAll(search, sort, null, null));
        req.setAttribute("search", search != null ? search : "");
        req.setAttribute("sort", sort != null ? sort : "name_asc");
        // Pre-select a material when arriving from a "+ Stock" link.
        req.setAttribute("selectedMaterialId", req.getParameter("materialId"));
        req.getRequestDispatcher("/WEB-INF/views/inventory/inventory_list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Long materialId = Long.parseLong(req.getParameter("materialId"));
        BigDecimal quantity = new BigDecimal(req.getParameter("quantity"));
        String movement = req.getParameter("movement");
        String note = req.getParameter("note");

        try {
            switch (movement) {
                case "PURCHASE"       -> inventoryService.purchase(materialId, quantity, user.getId(), note);
                case "ADJUSTMENT_ADD" -> inventoryService.adjust(materialId, quantity, user.getId(), note);
                case "ADJUSTMENT_SUB" -> inventoryService.adjust(materialId, quantity.negate(), user.getId(), note);
                case "WASTE"          -> inventoryService.recordWaste(materialId, quantity, user.getId(), note);
                case "INITIAL_STOCK"  -> inventoryService.setInitialStock(materialId, quantity, user.getId());
            }
            resp.sendRedirect(req.getContextPath() + "/inventory?success=1");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }
}
