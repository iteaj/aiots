package com.iteaj.iboot.plugin.datasource.rdbms.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.data.DefaultRealtimeData;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeListenerFilter;
import com.iteaj.framework.spi.iot.table.TableMetaResolver;
import com.iteaj.iot.tools.db.FieldMeta;
import com.iteaj.iot.tools.db.rdbms.DefaultRdbmsSqlManager;
import com.iteaj.iot.tools.db.rdbms.RdbmsMeta;

import java.util.List;
import java.util.Objects;

import static com.iteaj.framework.spi.iot.data.RealtimeCollectData.COLLECT_TABLE_NAME;

public class RdbmsRealtimeDataListener implements RealtimeListenerFilter {

    private static final RdbmsMeta rdbmsMeta;
    private final DefaultRdbmsSqlManager rdbmsSqlManager;

    static {
        List<FieldMeta> fieldMetas = TableMetaResolver.resolveEntityTypeToMetas(DefaultRealtimeData.class);
        rdbmsMeta = new RdbmsMeta(COLLECT_TABLE_NAME, fieldMetas);
    }

    public RdbmsRealtimeDataListener(DefaultRdbmsSqlManager rdbmsSqlManager) {
        this.rdbmsSqlManager = rdbmsSqlManager;
        rdbmsSqlManager.register(rdbmsMeta);
    }

    @Override
    public void doIotEvent(IotEventType eventType, RealtimePayload payload) {
        if(isFilter(payload.getType(), payload)) {
            List collect = (List) payload.getPayload();
            if(CollectionUtil.isNotEmpty(collect)) {
                rdbmsSqlManager.batchInsert(COLLECT_TABLE_NAME, collect);
            }
        }
    }

    @Override
    public String getFilterName() {
        return FILTER_DATABASE;
    }

    @Override
    public boolean isFilter(String type, RealtimePayload payload) {
        return Objects.equals(type, "data_full");
    }
}
