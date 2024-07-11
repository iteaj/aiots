package com.iteaj.iboot.plugin.message.listener;

import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.plugin.message.entity.MessageTemplate;
import com.iteaj.iboot.plugin.message.service.IMessageTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 监听告警信息, 并且发送消息
 */
@Component
@AllArgsConstructor
public class WarnSendMsgListener implements RealtimeInfoListener {

    private final IMessageTemplateService templateService;

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        if(value.getType().equals("warn")) {
            value.getPayload().forEach(warn -> {
                if(warn instanceof WarnData) {
                    templateService.sendIotWarnMessage((WarnData) warn);
                }
            });
        }
    }
}
