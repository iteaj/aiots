package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RealtimeMethodProvide {
    public static List<ScriptComplete> completes = Arrays.asList(
            new ScriptComplete("methods.isUid(data, 'uid')", "是否为指定设备",2),
            new ScriptComplete("methods.isProduct(data, 'productCode')", "是否为指定产品",2),
            new ScriptComplete("methods.isProtocol(data, 'protocolCode')", "是否为指定协议",2),
            new ScriptComplete("methods.isRealtimeData(data)", "是否为实时数据",2),
            new ScriptComplete("methods.isRealtimeStatus(data)", "是否为设备状态",2),
            new ScriptComplete("data.getUid()", "获取设备uid",3),
            new ScriptComplete("data.getValue()", "获取点位数据",3),
            new ScriptComplete("data.getField()", "获取点位属性",3),
            new ScriptComplete("console.log('')", "打印调试日志",4),
            new ScriptComplete("console.info('')", "打印信息日志",4),
            new ScriptComplete("console.warn('')", "打印告警日志",4),
            new ScriptComplete("console.error('')", "打印错误日志",4)
        );

    public boolean isProduct(Object data, String productCode) {
        if(this.isRealtimeData(data)) {
            return Objects.equals(getData(data).getProductCode(), productCode);
        }

        if(this.isRealtimeStatus(data)) {
            return Objects.equals(getData(data).getProductCode(), productCode);
        }

        return false;
    }

    public RealtimeCollectData getData(Object data) {
        return (RealtimeCollectData) data;
    }

    public RealtimeDeviceStatus getStatus(Object data) {
        return (RealtimeDeviceStatus) data;
    }

    public boolean isRealtimeData(Object data) {
        return data instanceof RealtimeCollectData;
    }

    public boolean isRealtimeStatus(Object data) {
        return data instanceof RealtimeDeviceStatus;
    }
}
