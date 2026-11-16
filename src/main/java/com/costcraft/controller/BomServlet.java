package com.costcraft.controller;

import com.costcraft.entity.BomItem;
import com.costcraft.entity.User;
import com.costcraft.service.MaterialService;
import com.costcraft.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/bom")
public class BomServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final MaterialService materialService = new MaterialService();

    // Editing recipes (BOM) is owner-only — workers should not change formulas.
    private boolean isOwner(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        User u = (s != null) ? (User) s.getAttribute("user") : null;
        return u != null && u.isOwner();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isOwner(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Long productId = Long.parseLong(req.getParameter("productId"));
        req.setAttribute("product", productService.findById(productId));
        req.setAttribute("bomItems", productService.getBom(productId));
        req.setAttribute("materials", materialService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/bom/bom_edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isOwner(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        Long productId = Long.parseLong(req.getParameter("productId"));
        try {
            if ("add".equals(action)) {
                BomItem bi = new BomItem();
                bi.setProductId(productId);
                bi.setMaterialId(Long.parseLong(req.getParameter("materialId")));
                bi.setGramQuantity(new BigDecimal(req.getParameter("gramQuantity")));
                bi.setDisplayOrder(0);
                productService.saveBomItem(bi);
            } else if ("update".equals(action)) {
                BomItem bi = new BomItem();
                bi.setId(Long.parseLong(req.getParameter("id")));
                bi.setProductId(productId);
                bi.setGramQuantity(new BigDecimal(req.getParameter("gramQuantity")));
                productService.saveBomItem(bi);
            } else if ("delete".equals(action)) {
                Long bomItemId = Long.parseLong(req.getParameter("id"));
                productService.deleteBomItem(bomItemId, productId);
            }
            resp.sendRedirect(req.getContextPath() + "/bom?productId=" + productId + "&success=1");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }
}
