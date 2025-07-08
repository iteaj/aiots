package com.iteaj.iboot.plugin.iotview.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.framework.spi.iot.view.ViewRealtimeData;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 将采集的数据实时推送到IotView大屏
 */
@Component
public class ViewRealtimePushListener implements RealtimeInfoListener, WebSocketServerListener {

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
                                            .append(item.getProductCode()).append(DataItemOption.SPLIT).append(item.getAttr());
                                    return isMatcher(binds, item, key);
                                }).collect(Collectors.toList());
                            } else {
                                result = data.stream().filter(item -> {
                                    StringBuilder key = new StringBuilder(ViewRealtimeData.STATUS_PREFIX).append(item.getProductCode());
                                    return isMatcher(binds, item, key);
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

    private boolean isMatcher(Set<String> binds, ViewRealtimeData item, StringBuilder key) {
        String keyValue = key.toString();
        if (binds.contains(keyValue)) {
            item.setKey(keyValue);
            return true;
        } else {
            keyValue = key.append(DataItemOption.SPLIT).append(item.getUid()).toString();
            if (binds.contains(keyValue)) {
                item.setKey(keyValue);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        if(value.isData() || value.isDataActive()) {
            final List<ViewRealtimeData> datas = (List<ViewRealtimeData>) value.getPayload().stream()
                    .filter(item -> item != null && ((RealtimeCollectData)item).getStatus())
                    .map(item -> {
                        RealtimeCollectData data = (RealtimeCollectData) item;
                        return ViewRealtimeData.buildAttr(data.getProductCode(), data.getUid(), data.getField(), data.getValue());
                    }).collect(Collectors.toList());
            this.push(datas, ViewRealtimeData.Attr_PREFIX);
        } else if(value.isWarn()) {

        } else if(value.isStatus()) {
            final List<ViewRealtimeData> datas = (List<ViewRealtimeData>) value.getPayload().stream()
                    .filter(item -> item != null)
                    .map((item) -> {
                        RealtimeDeviceStatus status = (RealtimeDeviceStatus) item;
                        return ViewRealtimeData.buildStatus(status.getProductCode(), status.getUid(), status.getDeviceStatus());
                    }).collect(Collectors.toList());
            this.push(datas, ViewRealtimeData.STATUS_PREFIX);
        }
    }
}
