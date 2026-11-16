package com.costcraft.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MaterialPriceSubject {

    private final List<PriceObserver> observers = new ArrayList<>();

    public void register(PriceObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Connection conn) {
        for (PriceObserver observer : observers) {
            observer.onPriceChanged(conn);
        }
    }
}
