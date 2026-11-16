package com.costcraft.controller;

import com.costcraft.entity.ProductionBatch;
import com.costcraft.entity.User;
import com.costcraft.service.BatchService;
import com.costcraft.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/batches")
public class BatchServlet extends HttpServlet {

    private final BatchService batchService = new BatchService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("new".equals(action)) {
            String productIdStr = req.getParameter("productId");
            if (productIdStr != null) {
                Long productId = Long.parseLong(productIdStr);
                req.setAttribute("product", productService.findById(productId));
                req.setAttribute("bomItems", productService.getBom(productId));
                req.setAttribute("breakdown", productService.getUnitCostBreakdown(productId));
            }
            req.setAttribute("products", productService.findActive());
            req.getRequestDispatcher("/WEB-INF/views/batch/batch_form.jsp").forward(req, resp);

        } else if ("export".equals(action)) {
            exportCsv(req, resp);

        } else {
            // Filtered list
            String productIdStr = req.getParameter("productId");
            String dateFrom     = req.getParameter("dateFrom");
            String dateTo       = req.getParameter("dateTo");
            String worker       = req.getParameter("worker");
            Long productId = (productIdStr != null && !productIdStr.isBlank())
                    ? Long.parseLong(productIdStr) : null;

            boolean filtered = productId != null
                    || (dateFrom != null && !dateFrom.isBlank())
                    || (dateTo   != null && !dateTo.isBlank())
                    || (worker   != null && !worker.isBlank());

            List<ProductionBatch> batches = filtered
                    ? batchService.findFiltered(productId, dateFrom, dateTo, worker)
                    : batchService.findAll(200);

            req.setAttribute("batches",    batches);
            req.setAttribute("products",   productService.findActive());
            req.setAttribute("productId",  productIdStr != null ? productIdStr : "");
            req.setAttribute("dateFrom",   dateFrom != null ? dateFrom : "");
            req.setAttribute("dateTo",     dateTo   != null ? dateTo   : "");
            req.setAttribute("worker",     worker   != null ? worker   : "");
            req.getRequestDispatcher("/WEB-INF/views/batch/batch_list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Long productId = Long.parseLong(req.getParameter("productId"));
        BigDecimal multiplier = new BigDecimal(req.getParameter("multiplier"));
        String notes = req.getParameter("notes");

        Map<Long, BigDecimal> overrides = new HashMap<>();
        req.getParameterMap().forEach((key, values) -> {
            if (key.startsWith("override_")) {
                try {
                    Long bomItemId = Long.parseLong(key.substring(9));
                    if (values.length > 0 && !values[0].isBlank()) {
                        overrides.put(bomItemId, new BigDecimal(values[0]));
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        try {
            long batchId = batchService.recordBatch(productId, multiplier, overrides, user.getId(), notes);
            resp.sendRedirect(req.getContextPath() + "/batches?success=" + batchId);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("products", productService.findActive());
            req.getRequestDispatcher("/WEB-INF/views/batch/batch_form.jsp").forward(req, resp);
        }
    }

    private void exportCsv(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String productIdStr = req.getParameter("productId");
        String dateFrom     = req.getParameter("dateFrom");
        String dateTo       = req.getParameter("dateTo");
        String worker       = req.getParameter("worker");
        Long productId = (productIdStr != null && !productIdStr.isBlank())
                ? Long.parseLong(productIdStr) : null;

        List<ProductionBatch> batches = batchService.findFiltered(productId, dateFrom, dateTo, worker);

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"batch_history.csv\"");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        PrintWriter out = resp.getWriter();
        out.println("Date,Product,Multiplier,Units Produced,Unit Cost (IDR),Total Cost (IDR),Worker,Notes");
        for (ProductionBatch b : batches) {
            out.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    sdf.format(b.getCreatedAt()),
                    esc(b.getProductName()),
                    b.getMultiplier().toPlainString(),
                    b.getUnitsProduced().toPlainString(),
                    b.getRecordedUnitCost().toPlainString(),
                    b.getRecordedTotalCost().toPlainString(),
                    esc(b.getWorkerUsername()),
                    esc(b.getNotes() != null ? b.getNotes() : ""));
        }
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\"", "\"\"");
    }
}
