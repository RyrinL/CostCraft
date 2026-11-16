package com.costcraft.controller;

import com.costcraft.entity.CurrencyRate;
import com.costcraft.entity.Product;
import com.costcraft.entity.User;
import com.costcraft.service.CurrencyService;
import com.costcraft.service.ProductService;
import com.costcraft.service.ShoppingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/shopping-list")
public class ShoppingListServlet extends HttpServlet {

    private final ShoppingService shoppingService = new ShoppingService();
    private final ProductService productService = new ProductService();
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Product> products = productService.findActive();
        req.setAttribute("products", products);

        // Read "batches_<productId>" inputs into a plan map, and keep the raw
        // values so the form can be repopulated after submit.
        Map<Long, BigDecimal> plan = new LinkedHashMap<>();
        Map<Long, String> planRaw = new LinkedHashMap<>();
        for (Product p : products) {
            String v = req.getParameter("batches_" + p.getId());
            planRaw.put(p.getId(), v != null ? v.trim() : "");
            if (v != null && !v.isBlank()) {
                try {
                    BigDecimal b = new BigDecimal(v.trim());
                    if (b.signum() > 0) plan.put(p.getId(), b);
                } catch (NumberFormatException ignore) { /* skip non-numeric */ }
            }
        }

        req.setAttribute("planRaw", planRaw);
        req.setAttribute("hasPlan", !plan.isEmpty());
        req.setAttribute("items", shoppingService.buildList(plan));

        // Expose the user's display-currency params so the plan form can show a
        // live cost estimate (converted + formatted) as she types.
        User user = (req.getSession(false) != null) ? (User) req.getSession(false).getAttribute("user") : null;
        String code = (user != null && user.getDisplayCurrency() != null) ? user.getDisplayCurrency() : "IDR";
        CurrencyRate rate = currencyService.findByCode(code);
        if (rate != null) {
            req.setAttribute("curSymbol", rate.getSymbol());
            req.setAttribute("curRate", rate.getRateToBase());
            req.setAttribute("curDecimals", rate.getDecimalPlaces());
        } else {
            req.setAttribute("curSymbol", "Rp ");
            req.setAttribute("curRate", BigDecimal.ONE);
            req.setAttribute("curDecimals", 0);
        }

        req.getRequestDispatcher("/WEB-INF/views/shopping/shopping_list.jsp").forward(req, resp);
    }
}
