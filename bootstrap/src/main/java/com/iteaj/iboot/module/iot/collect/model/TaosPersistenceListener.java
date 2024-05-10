package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.iot.taos.TaosSqlManager;

import java.util.List;

public class TaosPersistenceListener implements DataPersistenceListener {

    private final TaosSqlManager taosSqlManager;

    public TaosPersistenceListener(TaosSqlManager taosSqlManager) {
        this.taosSqlManager = taosSqlManager;
    }

    @Override
    public void persistence(List collectData) {

    }
}
