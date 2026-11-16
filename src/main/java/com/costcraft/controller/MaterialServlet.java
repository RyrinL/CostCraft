package com.costcraft.controller;

import com.costcraft.entity.Material;
import com.costcraft.entity.User;
import com.costcraft.service.MaterialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/materials")
public class MaterialServlet extends HttpServlet {

    private final MaterialService materialService = new MaterialService();

    // Workers may view materials, but only owners may create/edit/delete them.
    private boolean isOwner(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        User u = (s != null) ? (User) s.getAttribute("user") : null;
        return u != null && u.isOwner();
    }

    // Redirect back to the list, preserving the filter (fSearch/fCat/fStatus/fSort)
    // that was carried through the edit form so the user lands where they left off.
    private String backToList(HttpServletRequest req, String success) {
        StringBuilder q = new StringBuilder();
        appendParam(q, "search",     req.getParameter("fSearch"));
        appendParam(q, "categoryId", req.getParameter("fCat"));
        appendParam(q, "status",     req.getParameter("fStatus"));
        appendParam(q, "sort",       req.getParameter("fSort"));
        appendParam(q, "success",    success);
        return req.getContextPath() + "/materials" + (q.length() > 0 ? "?" + q : "");
    }

    private void appendParam(StringBuilder q, String key, String value) {
        if (value == null || value.isBlank()) return;
        if (q.length() > 0) q.append('&');
        q.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (("new".equals(action) || "edit".equals(action)) && !isOwner(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            if ("new".equals(action)) {
                req.setAttribute("categories", materialService.findAllCategories());
                req.getRequestDispatcher("/WEB-INF/views/material/material_form.jsp").forward(req, resp);
            } else if ("edit".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                req.setAttribute("material", materialService.findById(id));
                req.setAttribute("categories", materialService.findAllCategories());
                req.getRequestDispatcher("/WEB-INF/views/material/material_form.jsp").forward(req, resp);
            } else {
                String search  = req.getParameter("search");
                String sort    = req.getParameter("sort");
                String status  = req.getParameter("status");
                String catStr  = req.getParameter("categoryId");
                Long categoryId = (catStr != null && !catStr.isBlank()) ? Long.parseLong(catStr) : null;
                req.setAttribute("materials",  materialService.findAll(search, sort, status, categoryId));
                req.setAttribute("categories", materialService.findAllCategories());
                req.setAttribute("search",     search != null ? search : "");
                req.setAttribute("sort",       sort   != null ? sort   : "");
                req.setAttribute("status",     status != null ? status : "");
                req.setAttribute("categoryId", catStr != null ? catStr : "");
                req.getRequestDispatcher("/WEB-INF/views/material/material_list.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("materials", materialService.findAll());
            req.setAttribute("search", ""); req.setAttribute("sort", ""); req.setAttribute("status", "");
            req.getRequestDispatcher("/WEB-INF/views/material/material_list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isOwner(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                Material m = buildFromRequest(req);
                materialService.createMaterial(m);
                resp.sendRedirect(backToList(req, "created"));
            } else if ("update".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                String name = req.getParameter("name");
                String catStr = req.getParameter("categoryId");
                Long categoryId = (catStr != null && !catStr.isBlank()) ? Long.parseLong(catStr) : null;
                String unit = req.getParameter("unit");
                BigDecimal packageSize = new BigDecimal(req.getParameter("packageSize"));
                BigDecimal packagePrice = new BigDecimal(req.getParameter("packagePrice"));
                BigDecimal threshold = parseDecimal(req.getParameter("lowStockThreshold"));
                materialService.updateFull(id, name, categoryId, unit, packageSize, packagePrice, threshold);
                resp.sendRedirect(backToList(req, "updated"));
            } else if ("delete".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                materialService.deleteMaterial(id);
                resp.sendRedirect(backToList(req, "deleted"));
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }

    private Material buildFromRequest(HttpServletRequest req) {
        Material m = new Material();
        m.setCategoryId(Long.parseLong(req.getParameter("categoryId")));
        m.setName(req.getParameter("name"));
        m.setPackageSize(new BigDecimal(req.getParameter("packageSize")));
        m.setPackagePrice(new BigDecimal(req.getParameter("packagePrice")));
        m.setUnit(req.getParameter("unit") != null ? req.getParameter("unit") : "gram");
        m.setLowStockThreshold(parseDecimal(req.getParameter("lowStockThreshold")));
        m.setCurrentStock(BigDecimal.ZERO);
        return m;
    }

    private BigDecimal parseDecimal(String val) {
        if (val == null || val.isBlank()) return BigDecimal.ZERO;
        try { return new BigDecimal(val); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }
}
