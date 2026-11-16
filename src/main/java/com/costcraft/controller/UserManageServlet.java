package com.costcraft.controller;

import com.costcraft.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/users")
public class UserManageServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/user/user_form.jsp").forward(req, resp);
        } else if ("edit".equals(action)) {
            Long id = Long.parseLong(req.getParameter("id"));
            req.setAttribute("editUser", userService.findById(id));
            req.getRequestDispatcher("/WEB-INF/views/user/user_form.jsp").forward(req, resp);
        } else {
            req.setAttribute("users", userService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/user/user_list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                userService.createUser(
                        req.getParameter("username"),
                        req.getParameter("password"),
                        req.getParameter("role"),
                        req.getParameter("email")
                );
                resp.sendRedirect(req.getContextPath() + "/users?success=created");
            } else if ("update".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                userService.updateUser(id,
                        req.getParameter("username"),
                        req.getParameter("role"),
                        req.getParameter("email"),
                        "true".equals(req.getParameter("active")));
                resp.sendRedirect(req.getContextPath() + "/users?success=updated");
            } else if ("password".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                userService.changePassword(id, req.getParameter("newPassword"));
                resp.sendRedirect(req.getContextPath() + "/users?success=password");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("users", userService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/user/user_list.jsp").forward(req, resp);
        }
    }
}
