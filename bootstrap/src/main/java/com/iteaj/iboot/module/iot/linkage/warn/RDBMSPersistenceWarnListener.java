package com.iteaj.iboot.module.iot.linkage.warn;

import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;

/**
 * 监听告警数据并实时做持久化入库
 */
public class RDBMSPersistenceWarnListener implements LinkageActionListener {

    private final IWarnRecordService warnRecordService;

    public RDBMSPersistenceWarnListener(IWarnRecordService warnRecordService) {
        this.warnRecordService = warnRecordService;
    }

    @Override
    public void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status) {
        warnRecordService.save(record);
    }
}
