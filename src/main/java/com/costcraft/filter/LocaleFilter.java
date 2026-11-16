package com.costcraft.filter;

import com.costcraft.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.jsp.jstl.core.Config;
import jakarta.servlet.jsp.jstl.fmt.LocalizationContext;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

// Registered in web.xml — runs after encoding, before auth.
public class LocaleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;
        String lang = (user != null) ? user.getLanguage() : "en";
        Locale locale = Locale.of(lang);
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("messages", locale);
        } catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
        }
        Config.set(request, Config.FMT_LOCALE, locale);
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(bundle, locale));
        chain.doFilter(request, response);
    }
}
