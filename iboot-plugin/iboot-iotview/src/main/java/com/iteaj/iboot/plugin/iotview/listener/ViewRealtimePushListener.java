package com.iteaj.iboot.plugin.iotview.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.framework.spi.iot.listener.*;
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
public class ViewRealtimePushListener implements WebSocketServerListener, IotListener<IotEventPayload> {

    private static final String URI = "/ws/iot/view";
    private static final String ONLINE = "在线";
    private static final String OFFLINE = "离线";
    private final DefaultWebSocketServerComponent webSocketServerComponent;
    private final IModelAttrRealtimeDataService modelAttrRealtimeDataService;
    private AttributeKey<Set<String>> bindsAttr = AttributeKey.valueOf("ViewBindsAttr");
    private Logger logger = LoggerFactory.getLogger(getClass());

    public ViewRealtimePushListener(DefaultWebSocketServerComponent webSocketServerComponent
            , IModelAttrRealtimeDataService modelAttrRealtimeDataService) {
        this.webSocketServerComponent = webSocketServerComponent;
        this.modelAttrRealtimeDataService = modelAttrRealtimeDataService;
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
                if(!binds.isEmpty()) {
                    List<ViewRealtimeData> result = new ArrayList<>();
                    binds.forEach(item -> {
                        final String[] split = item.split(DataItemOption.SPLIT);
                        if(item.startsWith(ViewRealtimeData.Attr_PREFIX)) {
                            if(split.length == 4) {
                                final ModelAttrRealtimeData realtimeData = modelAttrRealtimeDataService.getRealtimeData(split[3], split[2]);
                                if(realtimeData != null) {
                                    final ViewRealtimeData initAttr = ViewRealtimeData.initAttr(item
                                            , realtimeData.getRealtime().getCollectTime()
                                            , realtimeData.getRealtime().getValue());
                                    result.add(initAttr);
                                } else {
                                    result.add(ViewRealtimeData.initAttr(item, null, ""));
                                }
                            }
                        } else if(item.startsWith(ViewRealtimeData.STATUS_PREFIX)) {
                            final RealtimeDeviceStatus deviceStatus = modelAttrRealtimeDataService.getDeviceStatus(split[2]);
                            if(deviceStatus != null) {
                                String value = deviceStatus.getDeviceStatus().equals("online") ? ONLINE : OFFLINE;
                                final ViewRealtimeData initStatus = ViewRealtimeData.initStatus(item
                                        , deviceStatus.getUpdateTime(), value);
                                result.add(initStatus);
                            } else {
                                result.add(ViewRealtimeData.initStatus(item, null, ""));
                            }
                        } else {
                            final ViewRealtimeData initStatus = ViewRealtimeData.initWarn(item, null, "");
                            result.add(initStatus);
                        }
                    });

                    if(CollectionUtil.isNotEmpty(result)) {
                        protocol.response(JSON.toJSONString(result));
                    }
                }
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
                            List<ViewRealtimeData> result = null;
                            if(type.equals(ViewRealtimeData.Attr_PREFIX)) {
                                result = data.stream().filter(item -> {
                                    StringBuilder key = new StringBuilder(ViewRealtimeData.Attr_PREFIX)
                                            .append(item.getProductCode()).append(DataItemOption.SPLIT)
                                            .append(item.getAttr()).append(DataItemOption.SPLIT).append(item.getUid());
                                    return isMatcher(binds, item, key);
                                }).collect(Collectors.toList());
                            } else if(type.equals(ViewRealtimeData.STATUS_PREFIX)){
                                result = data.stream().filter(item -> {
                                    StringBuilder key = new StringBuilder(ViewRealtimeData.STATUS_PREFIX)
                                            .append(item.getProductCode()).append(DataItemOption.SPLIT).append(item.getUid());
                                    return isMatcher(binds, item, key);
                                }).collect(Collectors.toList());
                            } else if(type.equals(ViewRealtimeData.WARN_PREFIX)) {
                                final String allKey = ViewRealtimeData.WARN_PREFIX + "all";
                                if(binds.contains(allKey)) {
                                    result = data.stream().map(item -> item.setKey(allKey)).collect(Collectors.toList());
                                } else {
                                    result = data.stream().filter(item -> {
                                        StringBuilder key = new StringBuilder(ViewRealtimeData.WARN_PREFIX).append(item.getProductCode());
                                        String keyValue = key.toString();
                                        if(binds.contains(keyValue)) {
                                            item.setKey(keyValue);
                                            return true;
                                        }

                                        keyValue = key.append(DataItemOption.SPLIT).append(item.getUid()).toString();
                                        if(binds.contains(keyValue)) {
                                            item.setKey(keyValue);
                                            return true;
                                        }

                                        return false;
                                    }).collect(Collectors.toList());
                                }
                            }

                            if(result != null && result.size() > 0) {
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
        }

        return false;
    }

    @Override
    public void onIotEvent(IotEventType eventType, IotEventPayload payload) {
        if(eventType == IotEventType.Realtime) {
            RealtimePayload value = (RealtimePayload) payload;
            if(value.isData() || value.isDataActive() || value.isDataCmd()) {
                final List<ViewRealtimeData> datas = (List<ViewRealtimeData>) value.getPayload().stream()
                        .filter(item -> item != null && ((RealtimeCollectData)item).getStatus())
                        .map(item -> {
                            RealtimeCollectData data = (RealtimeCollectData) item;
                            return ViewRealtimeData.buildAttr(data.getProductCode(), data.getUid(), data.getField(), data.getValue())
                                    .setCollectTime(data.getCollectTime());
                        }).collect(Collectors.toList());
                this.push(datas, ViewRealtimeData.Attr_PREFIX);
            } else if(value.isWarn()) {
                final List<ViewRealtimeData> datas = (List<ViewRealtimeData>) value.getPayload().stream()
                        .filter(item -> item != null)
                        .map(item -> {
                            WarnData data = (WarnData) item;
                            return ViewRealtimeData.buildWarn(data.getProductCode(), data.getUid(), data.getField(), data.getContent())
                                    .setCollectTime(data.getWarnTime());
                        }).collect(Collectors.toList());

                this.push(datas, ViewRealtimeData.WARN_PREFIX);
            } else if(value.isStatus()) {
                final List<ViewRealtimeData> datas = (List<ViewRealtimeData>) value.getPayload().stream()
                        .filter(item -> item != null)
                        .map((item) -> {
                            RealtimeDeviceStatus status = (RealtimeDeviceStatus) item;
                            String content = status.getDeviceStatus().equals("online") ? ONLINE : OFFLINE;
                            return ViewRealtimeData.buildStatus(status.getProductCode(), status.getUid(), content)
                                    .setCollectTime(status.getUpdateTime());
                        }).collect(Collectors.toList());
                this.push(datas, ViewRealtimeData.STATUS_PREFIX);
            }
        } else if(eventType == IotEventType.ScriptInvoke) {
            ScriptInvokePayload value = (ScriptInvokePayload) payload;
            if(value.isWarn()) {
                WarnData data = value.getData();
                final ViewRealtimeData viewRealtimeData = ViewRealtimeData.buildWarn(data.getProductCode(), data.getUid(), data.getField(), data.getContent())
                        .setCollectTime(data.getWarnTime());

                this.push(Arrays.asList(viewRealtimeData), ViewRealtimeData.WARN_PREFIX);
            }
        }
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.ScriptInvoke || eventType == IotEventType.Realtime;
    }
}
