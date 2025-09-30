package com.iteaj.iboot.module.iot.collect.websocket;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewRealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.server.websocket.WebSocketChannelMatcher;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerComponent;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.text.View;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 将采集的数据实时推送到IotView大屏
 */
@Component
public class ViewRealtimePushListener implements WebSocketServerListener, EventGroupCollectListener
        , SignalCollectListener, DeviceStatusListener, ModelAttrListener {

    private static final String URI = "/ws/iot/view";
    private final DefaultWebSocketServerComponent webSocketServerComponent;
    private AttributeKey<Set<String>> bindsAttr = AttributeKey.valueOf("ViewBindsAttr");
    private Logger logger = LoggerFactory.getLogger(getClass());

    public ViewRealtimePushListener(DefaultWebSocketServerComponent webSocketServerComponent) {
        this.webSocketServerComponent = webSocketServerComponent;
    }

    @Override
    public String uri() {
        return URI;
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {
        final String channelId = protocol.requestMessage().getChannelId();
        final Channel channel = webSocketServerComponent.getDeviceManager().find(channelId);
        if(channel != null) {
            final String bindAttr = protocol.readText();
            if(bindAttr != null && bindAttr.startsWith("[") && bindAttr.endsWith("]")) {
                final Set<String> binds = JSONArray.parseArray(bindAttr)
                        .stream()
                        .map(item -> (String) item)
                        .collect(Collectors.toSet());
                channel.attr(bindsAttr).set(binds);
            } else {
                DefaultWebSocketServerProtocol.close(channelId, WebSocketCloseStatus.EMPTY, "参数错误");
            }
        }
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo device) {
        try {
            if(CollectionUtil.isNotEmpty(device.getSupplier().getValues())) {
                final RealtimeStatus status = device.getDevice();
                final List<ViewRealtimeData> collect = device.getSupplier().getValues().stream()
                        .filter(item -> item != null && item.getStatus() == CollectStatus.Success)
                        .map(value -> ViewRealtimeData.buildAttr(status.getProductCode(), status.getUid(), value.getSignalOrField(), value.getValue()))
                        .collect(Collectors.toList());

                push(collect, ViewRealtimeData.Attr_PREFIX);
            }
        } catch (Exception e) {
            logger.error("实时推送模型数据异常 {} - 设备: {}", e.getMessage(), device.getDevice().buildDeviceKey(), e);
        }
    }

    @Override
    public void supplier(RealtimeStatus device) {
        this.supplier(Arrays.asList(device));
    }

    @Override
    public void supplier(Collection<RealtimeStatus> devices) {
        if(CollectionUtil.isNotEmpty(devices)) {
            final List<ViewRealtimeData> collect = devices.stream().filter(item -> item != null)
                    .map(item -> ViewRealtimeData.buildStatus(item.getProductCode(), item.getUid(), item.getStatus().name()))
                    .collect(Collectors.toList());
            push(collect, ViewRealtimeData.STATUS_PREFIX);
        }
    }

    /**
     * 写出数据
     * @param data
     */
    public void push(List<ViewRealtimeData> data, String type) {
        if(CollectionUtil.isNotEmpty(data)) {
            webSocketServerComponent.group(URI).ifPresent(group -> {
                if(!group.isEmpty()) {
                    group.forEach(channel -> {
                        final Attribute<Set<String>> attr = channel.attr(bindsAttr);
                        final Set<String> binds = attr.get();
                        if(binds != null) {
                            List<ViewRealtimeData> result;
                            if(type.equals(ViewRealtimeData.Attr_PREFIX)) {
                                result = data.stream().filter(item -> {
                                    StringBuilder key = new StringBuilder(ViewRealtimeData.Attr_PREFIX)
                                            .append(DataItemOption.SPLIT).append(item.getProductCode())
                                            .append(DataItemOption.SPLIT).append(item.getAttr());
                                    final String keyValue = key.toString();
                                    if(binds.contains(keyValue)) {
                                        item.setKey(keyValue);
                                        return true;
                                    }

                                    return false;
                                }).collect(Collectors.toList());
                            } else {
                                result = data.stream().filter(item -> {
                                    StringBuilder key = new StringBuilder(ViewRealtimeData.STATUS_PREFIX)
                                            .append(DataItemOption.SPLIT).append(item.getProductCode());
                                    String keyValue = key.toString();
                                    if(binds.contains(keyValue)) {
                                        item.setKey(keyValue);
                                        return true;
                                    } else {
                                        keyValue = key.append(DataItemOption.SPLIT).append(item.getUid()).toString();
                                        if(binds.contains(keyValue)) {
                                            item.setKey(keyValue);
                                            return true;
                                        }
                                    }

                                    return false;
                                }).collect(Collectors.toList());
                            }

                            if(result.size() > 0) {
                                DefaultWebSocketServerProtocol.write(channel.id().asShortText(), JSON.toJSONString(result));
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        try {
            if(data.getStatus()) {
                final ViewRealtimeData realtimeData = ViewRealtimeData.buildAttr(device.getProductCode(), device.getUid().toString(), data.getField(), data.getValue());
                push(Arrays.asList(realtimeData), ViewRealtimeData.Attr_PREFIX);
            }

        } catch (Exception e) {
            logger.error("实时推送点位数据异常 {} - 设备: {}", e.getMessage(), device.buildKey(), e);
        }
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        if(CollectionUtil.isNotEmpty(supplier.getValues())) {
            final List<ViewRealtimeData> collect = supplier.getValues().stream()
                    .filter(item -> item != null && item.getStatus() == CollectStatus.Success)
                    .map(item -> ViewRealtimeData.buildAttr(device.getProductCode(), device.getUid(), item.getSignalOrField(), item.getValue()))
                    .collect(Collectors.toList());
            push(collect, ViewRealtimeData.Attr_PREFIX);
        }
    }

}
