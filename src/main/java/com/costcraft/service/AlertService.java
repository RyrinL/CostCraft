package com.costcraft.service;

import com.costcraft.dao.AlertDao;
import com.costcraft.entity.Alert;
import com.costcraft.exception.ServiceException;
import com.costcraft.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AlertService {

    private final AlertDao alertDao = new AlertDao();

    public List<Alert> getUnresolved() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return alertDao.findUnresolved(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to fetch alerts", e);
        }
    }

    public int countUnresolved() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return alertDao.countUnresolved(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to count alerts", e);
        }
    }

    public void resolve(Long alertId) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            alertDao.resolve(conn, alertId);
        } catch (SQLException e) {
            throw new ServiceException("Failed to resolve alert", e);
        }
    }
}
