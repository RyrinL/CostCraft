package com.costcraft.controller;

import com.costcraft.entity.Product;
import com.costcraft.entity.User;
import com.costcraft.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    // Workers may view the product list, but only owners may create/edit/delete.
    private boolean isOwner(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        User u = (s != null) ? (User) s.getAttribute("user") : null;
        return u != null && u.isOwner();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (("new".equals(action) || "edit".equals(action)) && !isOwner(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if ("new".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/product/product_form.jsp").forward(req, resp);
        } else if ("edit".equals(action)) {
            Long id = Long.parseLong(req.getParameter("id"));
            req.setAttribute("product", productService.findById(id));
            req.getRequestDispatcher("/WEB-INF/views/product/product_form.jsp").forward(req, resp);
        } else {
            req.setAttribute("products", productService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/product/product_list.jsp").forward(req, resp);
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
                Product p = buildFromRequest(req);
                productService.createProduct(p);
                resp.sendRedirect(req.getContextPath() + "/products?success=created");
            } else if ("update".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                Product p = buildFromRequest(req);
                p.setId(id);
                productService.updateProduct(p);
                resp.sendRedirect(req.getContextPath() + "/products?success=updated");
            } else if ("delete".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                productService.deleteProduct(id);
                resp.sendRedirect(req.getContextPath() + "/products?success=deleted");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }

    private Product buildFromRequest(HttpServletRequest req) {
        Product p = new Product();
        p.setName(req.getParameter("name"));
        p.setYieldPerBatch(new BigDecimal(req.getParameter("yieldPerBatch")));
        String yu = req.getParameter("yieldUnit");
        p.setYieldUnit(yu != null && !yu.isBlank() ? yu : "pcs");
        p.setDaysPerBatch(parseDecimal(req.getParameter("daysPerBatch"), "0.5"));
        p.setMarginLowerPct(parseDecimal(req.getParameter("marginLowerPct"), "0.30"));
        p.setMarginUpperPct(parseDecimal(req.getParameter("marginUpperPct"), "0.50"));
        p.setActive(!"false".equals(req.getParameter("active")));
        return p;
    }

    private BigDecimal parseDecimal(String val, String defaultVal) {
        try { return new BigDecimal(val != null && !val.isBlank() ? val : defaultVal); }
        catch (NumberFormatException e) { return new BigDecimal(defaultVal); }
    }
}
