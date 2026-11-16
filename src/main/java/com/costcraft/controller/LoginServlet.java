package com.costcraft.controller;

import com.costcraft.entity.User;
import com.costcraft.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ── DEV ONLY: skip-login shortcut for testing (?dev=owner | ?dev=worker).
        String dev = req.getParameter("dev");
        if (dev != null) {
            String role = "worker".equalsIgnoreCase(dev) ? "WORKER" : "OWNER";
            User devUser = null;
            for (User u : userService.findAll()) {
                if (role.equals(u.getRole()) && u.isActive()) { devUser = u; break; }
            }
            if (devUser != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", devUser);
                session.setMaxInactiveInterval(3600);
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }
            req.setAttribute("error", "No active " + role + " account to skip into. Create one first.");
        }

        if (req.getSession(false) != null && req.getSession(false).getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = userService.login(username, password);
        if (user == null) {
            req.setAttribute("error", "Invalid username or password.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(3600);
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
