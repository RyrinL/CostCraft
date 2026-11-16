package com.costcraft.service;

import com.costcraft.dao.UserDao;
import com.costcraft.entity.User;
import com.costcraft.exception.ServiceException;
import com.costcraft.exception.ValidationException;
import com.costcraft.util.DBUtil;
import com.costcraft.util.PasswordHasher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDao userDao = new UserDao();

    public User login(String username, String password) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            User user = userDao.findByUsername(conn, username);
            if (user == null) return null;
            return PasswordHasher.verify(password, user.getSalt(), user.getPasswordHash()) ? user : null;
        } catch (SQLException e) {
            throw new ServiceException("Login failed", e);
        }
    }

    public List<User> findAll() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return userDao.findAll(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list users", e);
        }
    }

    public User findById(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return userDao.findById(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find user", e);
        }
    }

    public void createUser(String username, String password, String role, String email) {
        if (username == null || username.isBlank()) throw new ValidationException("Username is required");
        if (password == null || password.isBlank()) throw new ValidationException("Password is required");
        if (!"OWNER".equals(role) && !"WORKER".equals(role)) throw new ValidationException("Invalid role");

        try (Connection conn = DBUtil.getInstance().getConnection()) {
            if (userDao.findByUsername(conn, username) != null)
                throw new ValidationException("Username already exists: " + username);

            String salt = PasswordHasher.generateSalt();
            String hash = PasswordHasher.hash(password, salt);

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(hash);
            user.setSalt(salt);
            user.setRole(role);
            user.setEmail(email);
            user.setDisplayCurrency("IDR");
            user.setActive(true);
            userDao.insert(conn, user);
        } catch (SQLException e) {
            throw new ServiceException("Failed to create user", e);
        }
    }

    public void updateUser(Long id, String username, String role, String email, boolean active) {
        if (username == null || username.isBlank()) throw new ValidationException("Username is required");
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            User user = userDao.findById(conn, id);
            if (user == null) throw new ServiceException("User not found");
            // Prevent removing last owner
            if ("OWNER".equals(user.getRole()) && !"OWNER".equals(role)) {
                if (userDao.countOwners(conn) <= 1)
                    throw new ValidationException("Cannot remove the last OWNER");
            }
            user.setUsername(username);
            user.setRole(role);
            user.setEmail(email);
            user.setActive(active);
            userDao.update(conn, user);
        } catch (SQLException e) {
            throw new ServiceException("Failed to update user", e);
        }
    }

    public void changePassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) throw new ValidationException("Password is required");
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            String salt = PasswordHasher.generateSalt();
            String hash = PasswordHasher.hash(newPassword, salt);
            userDao.updatePassword(conn, id, hash, salt);
        } catch (SQLException e) {
            throw new ServiceException("Failed to change password", e);
        }
    }

    public void updateDisplayCurrency(Long id, String currencyCode) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            userDao.updateDisplayCurrency(conn, id, currencyCode);
        } catch (SQLException e) {
            throw new ServiceException("Failed to update currency preference", e);
        }
    }

    public void updateLanguage(Long id, String language) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            userDao.updateLanguage(conn, id, language);
        } catch (SQLException e) {
            throw new ServiceException("Failed to update language preference", e);
        }
    }
}
