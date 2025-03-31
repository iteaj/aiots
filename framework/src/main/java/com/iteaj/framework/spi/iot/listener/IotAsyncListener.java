package com.iteaj.framework.spi.iot.listener;

import java.util.function.Consumer;

public interface IotAsyncListener<V> extends IotListener<V> {

    /**
     * 监听事件
     * @param eventType
     * @param value
     */
    default void onIotEvent(IotEventType eventType, V value, Consumer callback) {}
}
