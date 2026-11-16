package com.costcraft.filter;

import com.costcraft.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

// Registered in web.xml — runs last, only on Owner-only paths.
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;
        if (user == null || !user.isOwner()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Owner role required");
            return;
        }

        chain.doFilter(request, response);
    }
}
