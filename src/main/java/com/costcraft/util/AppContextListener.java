package com.costcraft.util;

import com.costcraft.dao.UserDao;
import com.costcraft.entity.User;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger log = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DBUtil db = DBUtil.getInstance();
            try (Connection conn = db.getConnection()) {
                UserDao userDao = new UserDao();
                if (userDao.countOwners(conn) == 0) {
                    String salt = PasswordHasher.generateSalt();
                    String hash = PasswordHasher.hash("admin123", salt);
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setPasswordHash(hash);
                    admin.setSalt(salt);
                    admin.setRole("OWNER");
                    admin.setEmail("admin@costcraft.local");
                    admin.setDisplayCurrency("IDR");
                    admin.setActive(true);
                    userDao.insert(conn, admin);
                    log.info("CostCraft: bootstrap admin user created (admin / admin123)");
                }
            }
            log.info("CostCraft: application started successfully");
        } catch (Exception e) {
            log.log(Level.SEVERE, "CostCraft: failed to initialize — check db.properties and database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBUtil.getInstance().close();
        log.info("CostCraft: connection pool closed");
    }
}
