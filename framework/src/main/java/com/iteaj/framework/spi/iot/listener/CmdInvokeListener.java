package com.iteaj.framework.spi.iot.listener;

/**
 * 指令调用监听器
 */
public interface CmdInvokeListener extends IotListener<CmdInvokePayload>{

    @Override
    default boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.CmdInvoke;
    }

    @Override
    void onIotEvent(IotEventType eventType, CmdInvokePayload value);
}
