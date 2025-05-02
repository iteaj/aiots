package com.iteaj.framework.spi.iot.listener;

/**
 * 由脚本触发的监听器
 */
public interface ScriptInvokeListener extends IotListener<ScriptInvokePayload>{

    @Override
    default boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.ScriptInvoke;
    }

    @Override
    void onIotEvent(IotEventType eventType, ScriptInvokePayload value);
}
