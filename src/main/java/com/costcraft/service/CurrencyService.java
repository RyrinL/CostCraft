package com.costcraft.service;

import com.costcraft.dao.CurrencyRateDao;
import com.costcraft.entity.CurrencyRate;
import com.costcraft.exception.ServiceException;
import com.costcraft.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CurrencyService {

    private final CurrencyRateDao dao = new CurrencyRateDao();

    public List<CurrencyRate> findAll() {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return dao.findAll(conn);
        } catch (SQLException e) {
            throw new ServiceException("Failed to list currencies", e);
        }
    }

    public CurrencyRate findByCode(String code) {
        try (Connection conn = DBUtil.getInstance().getConnection()) {
            return dao.findByCode(conn, code);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find currency", e);
        }
    }
}
