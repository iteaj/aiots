package com.iteaj.iboot.plugin.datasource.listener;

import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iot.taos.TaosSqlManager;
import com.iteaj.iot.taos.TaosSqlMeta;
import com.iteaj.iot.tools.annotation.IotTableIdMeta;
import com.iteaj.iot.tools.db.DefaultFieldMeta;
import com.iteaj.iot.tools.db.FieldMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TaosRealtimeDataListener implements RealtimeInfoListener {

    private final TaosSqlManager taosSqlManager;

    // 采集数据的超级表名
    private final static String REALTIME_DATA_STABLE = "iot_data";
    // 采集数据的数据子表名
    private final static String REALTIME_DATA_TABLE = "iot_data_${uid}";

    private final static List<FieldMeta> dataMetas = Arrays.asList(
//            new DefaultFieldMeta(0, "");
    );

    public TaosRealtimeDataListener(TaosSqlManager taosSqlManager) {
        this.taosSqlManager = taosSqlManager;
        taosSqlManager.register(new TaosSqlMeta(REALTIME_DATA_STABLE, REALTIME_DATA_TABLE, dataMetas, true));
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        if(Objects.equals(value.getType(), "data")) {
//            taosSqlManager.batchInsert()
        }
    }
}
