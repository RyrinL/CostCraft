package com.costcraft.service;

import java.sql.Connection;

public interface PriceObserver {
    void onPriceChanged(Connection conn);
}
