package com.costcraft.controller;

import com.costcraft.entity.User;
import com.costcraft.service.CurrencyService;
import com.costcraft.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

@WebServlet("/preferences")
public class PreferencesServlet extends HttpServlet {

    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "id");

    private final UserService userService = new UserService();
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("currencies", currencyService.findAll());
        req.getRequestDispatcher("/WEB-INF/views/user/preferences.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");

        String currency = req.getParameter("displayCurrency");
        if (currency != null && !currency.isBlank()) {
            userService.updateDisplayCurrency(user.getId(), currency);
            user.setDisplayCurrency(currency);
        }

        String language = req.getParameter("language");
        if (language != null && SUPPORTED_LANGUAGES.contains(language)) {
            userService.updateLanguage(user.getId(), language);
            user.setLanguage(language);
        }

        req.getSession().setAttribute("user", user);
        resp.sendRedirect(req.getContextPath() + "/preferences?success=1");
    }
}
