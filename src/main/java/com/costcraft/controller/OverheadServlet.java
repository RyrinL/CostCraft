package com.costcraft.controller;

import com.costcraft.entity.OverheadConfig;
import com.costcraft.service.OverheadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/overhead")
public class OverheadServlet extends HttpServlet {

    private final OverheadService overheadService = new OverheadService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("edit".equals(action)) {
            Long id = Long.parseLong(req.getParameter("id"));
            req.setAttribute("config", overheadService.findById(id));
        }
        req.setAttribute("overheadConfigs", overheadService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/overhead/overhead_config.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            OverheadConfig cfg = buildFromRequest(req);
            if ("create".equals(action)) {
                overheadService.create(cfg);
            } else if ("update".equals(action)) {
                cfg.setId(Long.parseLong(req.getParameter("id")));
                overheadService.update(cfg);
            } else if ("delete".equals(action)) {
                overheadService.delete(Long.parseLong(req.getParameter("id")));
                resp.sendRedirect(req.getContextPath() + "/overhead?success=1");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/overhead?success=1");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }

    private OverheadConfig buildFromRequest(HttpServletRequest req) {
        OverheadConfig cfg = new OverheadConfig();
        cfg.setName(req.getParameter("name"));
        cfg.setCostStage(req.getParameter("costStage"));
        cfg.setAllocationRule(req.getParameter("allocationRule"));
        cfg.setFixedAmount(parseDecimal(req.getParameter("fixedAmount")));
        cfg.setDailyAmount(parseDecimal(req.getParameter("dailyAmount")));
        String dpm = req.getParameter("daysPerMonth");
        cfg.setDaysPerMonth(dpm != null && !dpm.isBlank() ? Integer.parseInt(dpm) : 30);
        cfg.setPercentage(parseDecimal(req.getParameter("percentage")));
        cfg.setActive(!"false".equals(req.getParameter("active")));
        cfg.setDisplayOrder(0);
        return cfg;
    }

    private BigDecimal parseDecimal(String val) {
        if (val == null || val.isBlank()) return null;
        try { return new BigDecimal(val); } catch (NumberFormatException e) { return null; }
    }
}
