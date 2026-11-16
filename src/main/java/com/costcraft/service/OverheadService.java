package com.costcraft.service;

import com.costcraft.dao.OverheadConfigDao;
import com.costcraft.entity.OverheadConfig;
import com.costcraft.exception.ServiceException;
import com.costcraft.exception.ValidationException;
import com.costcraft.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OverheadService {

    private final OverheadConfigDao overheadConfigDao = new OverheadConfigDao();

    public List<OverheadConfig> findAll() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return overheadConfigDao.findAll(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list overhead configs", e);
        }
    }

    public OverheadConfig findById(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return overheadConfigDao.findById(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find overhead config", e);
        }
    }

    public Long create(OverheadConfig cfg) {
        validate(cfg);
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return overheadConfigDao.insert(conn, cfg);
        } catch (SQLException e) {
            throw new ServiceException("Failed to create overhead config", e);
        }
    }

    public void delete(Long id) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            overheadConfigDao.delete(conn, id);
        } catch (SQLException e) {
            throw new ServiceException("Failed to delete overhead config", e);
        }
    }

    public void update(OverheadConfig cfg) {
        validate(cfg);
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            overheadConfigDao.update(conn, cfg);
        } catch (SQLException e) {
            throw new ServiceException("Failed to update overhead config", e);
        }
    }

    private void validate(OverheadConfig cfg) {
        if (cfg.getName() == null || cfg.getName().isBlank())
            throw new ValidationException("Name is required");
        if (cfg.getCostStage() == null)
            throw new ValidationException("Cost stage is required");
        if (cfg.getAllocationRule() == null)
            throw new ValidationException("Allocation rule is required");

        switch (cfg.getAllocationRule()) {
            case "PER_BATCH_FIXED", "PER_UNIT_FIXED" -> {
                if (cfg.getFixedAmount() == null || cfg.getFixedAmount().compareTo(BigDecimal.ZERO) < 0)
                    throw new ValidationException("Fixed amount required for this rule");
            }
            case "DAILY_ALLOCATED" -> {
                if (cfg.getDailyAmount() == null || cfg.getDailyAmount().compareTo(BigDecimal.ZERO) < 0)
                    throw new ValidationException("Daily amount required for DAILY_ALLOCATED");
            }
            case "PERCENTAGE_OF_DIRECT" -> {
                if (cfg.getPercentage() == null || cfg.getPercentage().compareTo(BigDecimal.ZERO) < 0)
                    throw new ValidationException("Percentage required for PERCENTAGE_OF_DIRECT");
            }
        }
    }
}
