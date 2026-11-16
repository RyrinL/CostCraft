package com.costcraft.service.inventory;

import com.costcraft.dao.InventoryLogDao;
import com.costcraft.dao.MaterialDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class AdjustmentCommand implements StockMovementCommand {

    private final MaterialDao materialDao;
    private final InventoryLogDao logDao;
    private final Long materialId;
    private final BigDecimal delta; // positive = add, negative = remove
    private final Long userId;
    private final String note;

    public AdjustmentCommand(MaterialDao materialDao, InventoryLogDao logDao,
                             Long materialId, BigDecimal delta, Long userId, String note) {
        this.materialDao = materialDao;
        this.logDao = logDao;
        this.materialId = materialId;
        this.delta = delta;
        this.userId = userId;
        this.note = note;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        materialDao.addStock(conn, materialId, delta);
        logDao.insert(conn, materialId, delta, "ADJUSTMENT", null, null, note, userId);
    }

    @Override
    public void undo(Connection conn) throws SQLException {
        materialDao.addStock(conn, materialId, delta.negate());
        logDao.insert(conn, materialId, delta.negate(), "ADJUSTMENT", null, null, "Undo adjustment", userId);
    }

    @Override
    public String describe() { return "Adjust material #" + materialId + " by " + delta; }
}
