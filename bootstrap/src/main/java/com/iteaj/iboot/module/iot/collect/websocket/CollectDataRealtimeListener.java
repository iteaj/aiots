package com.iteaj.iboot.module.iot.collect.websocket;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.server.websocket.WebSocketChannelMatcher;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 将采集的数据实时推送到前端
 */
public class CollectDataRealtimeListener implements WebSocketServerListener, EventGroupCollectListener {

    private static final String URI = "/ws/iot/realtime";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String uri() {
        return URI;
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {

    }

    /**
     * 写出数据
     * @param device
     */
    public void write(CollectInfo.DeviceInfo device) {
        try {
            if(CollectionUtil.isNotEmpty(device.getSupplier().getValues())) {
                Map<String, SignalOrFieldValue> valueMap = device.getSupplier().getValues().stream()
                        .collect(Collectors.toMap(SignalOrFieldValue::getSignalOrField, item -> item));
                String jsonString = JSONObject.toJSONString(valueMap);
                DefaultWebSocketServerProtocol.writeGroup(URI, jsonString, new WebSocketChannelMatcher((channel, httpRequestWrapper) -> {
                    String uid = httpRequestWrapper.getQueryParam("uid").orElse(null);
                    if(StrUtil.isNotBlank(uid)) {
                        return Objects.equals(uid, device.getDevice().getUid().toString());
                    }

                    return false;
                }));
            }
        } catch (Exception e) {
            logger.error("实时推送到前端异常 {} - 设备: {}", e.getMessage(), device.getDevice().buildDeviceKey(), e);
        }
    }

    @Override
    public void listener(CollectInfo info) {
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            // 异步发送
            IotThreadManager.instance().getExecutorService().execute(() -> {
                info.getInfos().forEach(device -> {
                    write(device);
                });
            });
        }
    }
}
