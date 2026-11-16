package com.costcraft.service.inventory;

import com.costcraft.dao.InventoryLogDao;
import com.costcraft.dao.MaterialDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class PurchaseCommand implements StockMovementCommand {

    private final MaterialDao materialDao;
    private final InventoryLogDao logDao;
    private final Long materialId;
    private final BigDecimal quantity;
    private final Long userId;
    private final String note;

    public PurchaseCommand(MaterialDao materialDao, InventoryLogDao logDao,
                           Long materialId, BigDecimal quantity, Long userId, String note) {
        this.materialDao = materialDao;
        this.logDao = logDao;
        this.materialId = materialId;
        this.quantity = quantity;
        this.userId = userId;
        this.note = note;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        materialDao.addStock(conn, materialId, quantity);
        logDao.insert(conn, materialId, quantity, "PURCHASE", null, null, note, userId);
    }

    @Override
    public void undo(Connection conn) throws SQLException {
        materialDao.deductStock(conn, materialId, quantity);
        logDao.insert(conn, materialId, quantity.negate(), "ADJUSTMENT", null, null, "Undo purchase", userId);
    }

    @Override
    public String describe() { return "Purchase " + quantity + " units of material #" + materialId; }
}
