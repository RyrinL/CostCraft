package com.costcraft.service.inventory;

import java.sql.Connection;
import java.sql.SQLException;

public interface StockMovementCommand {
    void execute(Connection conn) throws SQLException;
    void undo(Connection conn) throws SQLException;
    String describe();
}
