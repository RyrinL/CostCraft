package com.costcraft.service.inventory;

import com.costcraft.dao.InventoryLogDao;
import com.costcraft.dao.MaterialDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class InitialStockCommand implements StockMovementCommand {

    private final MaterialDao materialDao;
    private final InventoryLogDao logDao;
    private final Long materialId;
    private final BigDecimal quantity;
    private final Long userId;

    public InitialStockCommand(MaterialDao materialDao, InventoryLogDao logDao,
                               Long materialId, BigDecimal quantity, Long userId) {
        this.materialDao = materialDao;
        this.logDao = logDao;
        this.materialId = materialId;
        this.quantity = quantity;
        this.userId = userId;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        materialDao.addStock(conn, materialId, quantity);
        logDao.insert(conn, materialId, quantity, "INITIAL_STOCK", null, null, "Initial stock entry", userId);
    }

    @Override
    public void undo(Connection conn) throws SQLException {
        materialDao.deductStock(conn, materialId, quantity);
        logDao.insert(conn, materialId, quantity.negate(), "ADJUSTMENT", null, null, "Undo initial stock", userId);
    }

    @Override
    public String describe() { return "Initial stock " + quantity + " for material #" + materialId; }
}
