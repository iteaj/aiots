package com.iteaj.iboot.plugin.message.listener;

import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.IotListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.spi.iot.listener.ScriptInvokePayload;
import com.iteaj.iboot.plugin.message.service.IMessageTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 监听告警信息, 并且发送消息
 */
@Component
@AllArgsConstructor
public class WarnSendMsgListener implements IotListener<Object> {

    private final IMessageTemplateService templateService;

    @Override
    public void onIotEvent(IotEventType eventType, Object value) {
        if(value instanceof RealtimePayload) {
            if(((RealtimePayload) value).isWarn()) {
                ((RealtimePayload) value).getPayload().forEach(warn -> {
                    if(warn instanceof WarnData) {
                        templateService.sendIotWarnMessage((WarnData) warn);
                    }
                });
            }
        } else if(value instanceof ScriptInvokePayload && ((ScriptInvokePayload) value).isWarn()) {
            templateService.sendWarnMessage(((ScriptInvokePayload) value).getData());
        }
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.Realtime || eventType == IotEventType.ScriptInvoke;
    }

}
