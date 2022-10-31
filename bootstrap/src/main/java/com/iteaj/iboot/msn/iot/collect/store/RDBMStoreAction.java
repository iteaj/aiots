package com.iteaj.iboot.msn.iot.collect.store;

import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.entity.CollectData;
import com.iteaj.iot.tools.db.rdbms.RdbmsSqlManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * 关系型数据库存储
 */
public class RDBMStoreAction extends StoreAction {

    @Autowired
    private RdbmsSqlManager rdbmsSqlManager;

    @Override
    public String getName() {
        return IotConsts.STORE_ACTION_RDBMS;
    }

    @Override
    public String getDesc() {
        return "关系型数据库";
    }

    @Override
    public void store(List<CollectData> data) {
        rdbmsSqlManager.batchInsert(CollectData.class, Arrays.asList(data.toArray()));
    }
}
