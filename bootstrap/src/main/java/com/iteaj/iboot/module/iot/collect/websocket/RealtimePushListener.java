package com.iteaj.iboot.module.iot.collect.websocket;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.server.websocket.WebSocketChannelMatcher;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 将采集的数据实时推送到前端
 */
public class RealtimePushListener implements WebSocketServerListener
        , EventGroupCollectListener, SignalCollectListener, LinkageActionListener {

    private static final String URI = "/ws/iot/realtime";
    private final RealtimeDataService realtimeDataService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public RealtimePushListener(RealtimeDataService realtimeDataService) {
        this.realtimeDataService = realtimeDataService;
    }

    @Override
    public String uri() {
        return URI;
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {

    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        IotThreadManager.instance().getDeviceManageEventExecutor().execute(() -> this.push(info));
    }

    public void push(RealtimeStatus device) {
        this.push(Arrays.asList(device));
    }

    public void push(Collection<RealtimeStatus> devices) {
        if(CollectionUtil.isNotEmpty(devices)) {
            devices.forEach(device -> {
                try {
                    String jsonString = JSONObject.toJSONString(PushData.buildStatus(device));
                    DefaultWebSocketServerProtocol.writeGroup(URI, jsonString, new WebSocketChannelMatcher((channel, httpRequestWrapper) -> {
                        String uid = httpRequestWrapper.getQueryParam("uid").orElse(null);
                        if(StrUtil.isNotBlank(uid)) {
                            return Objects.equals(uid, device.getUid());
                        }

                        return false;
                    }));
                } catch (Exception e) {
                    logger.error("推送实时状态异常 {} - 设备: {}", e.getMessage(), device.buildDeviceKey(), e);
                }
            });
        }
    }

    /**
     * 写出数据
     * @param device
     */
    public void push(CollectInfo.DeviceInfo device) {
        try {
            if(CollectionUtil.isNotEmpty(device.getSupplier().getValues())) {
                Map<String, SignalOrFieldValue> valueMap = device.getSupplier().getValues().stream()
                        .collect(Collectors.toMap(SignalOrFieldValue::getSignalOrField, item -> item));
                String jsonString = JSONObject.toJSONString(PushData.buildModel(valueMap));

                DefaultWebSocketServerProtocol.writeGroup(URI, jsonString, new WebSocketChannelMatcher((channel, httpRequestWrapper) -> {
                    String uid = httpRequestWrapper.getQueryParam("uid").orElse(null);
                    if(StrUtil.isNotBlank(uid)) {
                        return Objects.equals(uid, device.getDevice().getUid());
                    }

                    return false;
                }));
            }
        } catch (Exception e) {
            logger.error("实时推送模型数据异常 {} - 设备: {}", e.getMessage(), device.getDevice().buildDeviceKey(), e);
        }
    }

    public void push(String deviceUid, ModelApi api, InvokeResult result) {
        if(api.getFuncType() == FuncType.R) { // 读指令才需要更新数据
            ProtocolSupplierManager.getDataSupplier(result.getProtocol()).ifPresent(supplier -> {
                if(CollectionUtil.isNotEmpty(supplier.getValues())) {
                    Map<String, SignalOrFieldValue> valueMap = supplier.getValues().stream()
                            .collect(Collectors.toMap(SignalOrFieldValue::getSignalOrField, item -> item));
                    realtimeDataService.put(supplier); // 加入实时数据缓存

                    // websocket方式写数据到前端
                    String jsonString = JSONObject.toJSONString(PushData.buildModel(valueMap));
                    DefaultWebSocketServerProtocol.writeGroup(URI, jsonString, new WebSocketChannelMatcher((channel, httpRequestWrapper) -> {
                        String uid = httpRequestWrapper.getQueryParam("uid").orElse(null);
                        if(StrUtil.isNotBlank(uid)) {
                            return Objects.equals(uid, deviceUid);
                        }

                        return false;
                    }));
                }
            });
        }
    }

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        try {
            Map<String, SignalOrFieldValue> valueMap = new HashMap<>();
            valueMap.put(data.getAddress(), SignalOrFieldValue.build(data.getAddress(), data.getCollectTime(), data.getValue()).builder(data.getCollectStatus()));
            String jsonString = JSONObject.toJSONString(PushData.buildSignal(valueMap));

            DefaultWebSocketServerProtocol.writeGroup(URI, jsonString, new WebSocketChannelMatcher((channel, httpRequestWrapper) -> {
                String uid = httpRequestWrapper.getQueryParam("uid").orElse(null);
                if(StrUtil.isNotBlank(uid)) {
                    return Objects.equals(uid, device.getUid().toString());
                }

                return false;
            }));
        } catch (Exception e) {
            logger.error("实时推送点位数据异常 {} - 设备: {}", e.getMessage(), device.buildKey(), e);
        }
    }

    @Override
    public void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status) {
//        PushData.buildStatus()
    }

    @Override
    public void apiInvoke(ModelApi api, RealtimeStatus status, InvokeResult result) {

    }
}
