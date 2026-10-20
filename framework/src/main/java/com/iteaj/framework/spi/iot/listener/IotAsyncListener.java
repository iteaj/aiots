package com.iteaj.framework.spi.iot.listener;

import java.util.function.Consumer;

public interface IotAsyncListener<V> extends IotListener<V> {

    /**
     * 监听事件
     * @param eventType 事件类型
     * @param value 事件值
     */
    default void onIotEvent(IotEventType eventType, V value, Consumer<Object> callback) {
        onIotEvent(eventType, value);
    }
}
