package com.iteaj.iboot.plugin.message.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.framework.spi.message.*;
import com.iteaj.iboot.plugin.message.dto.WarnMessageTemplate;
import com.iteaj.iboot.plugin.message.entity.MessageTemplate;
import com.iteaj.iboot.plugin.message.mapper.MessageTemplateMapper;
import com.iteaj.iboot.plugin.message.service.IMessageTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息模板 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2024-07-09
 */
@Service
public class MessageTemplateServiceImpl extends BaseServiceImpl<MessageTemplateMapper, MessageTemplate> implements IMessageTemplateService {

    @Autowired
    private MessageManager messageManager;
    @Autowired
    private ThreadPoolTaskExecutor executor;

    private Logger logger = LoggerFactory.getLogger(MessageTemplateServiceImpl.class);

    @Override
    public SendTemplate getTemplateById(Serializable key) {
        return this.getBaseMapper().getTemplateById(key);
    }

    @Override
    public WarnMessageTemplate getWarnTemplateByWarnConfigId(Long warnConfigId, String uid) {
        return this.getBaseMapper().getWarnTemplateByWarnConfigId(warnConfigId, uid);
    }

    @Override
    public void sendIotWarnMessage(WarnData warn) {
        executor.execute(() -> {
            WarnMessageTemplate template = this.getWarnTemplateByWarnConfigId(warn.getWarnConfigId(), warn.getUid());
            if(template != null && CollectionUtil.isNotEmpty(template.getUsers())) {
                doSendWarnMsg(warn, template);
            }
        });
    }

    private void doSendWarnMsg(WarnData warn, WarnMessageTemplate template) {
        List<String> phones = template.getUsers().stream()
                .filter(item -> StrUtil.isNotBlank(item.getPhone()))
                .map(item -> item.getPhone()).distinct()
                .collect(Collectors.toList());
        MessageService service = messageManager.getDefault(MessageType.Sms.name());
        if(CollectionUtil.isNotEmpty(phones) && service != null) {
            try {
                SendModel sendModel = new SendModel();
                sendModel.setAccept(phones);
                sendModel.setContent(template.getContent());
                sendModel.setTitle(template.getTemplateTitle());
                sendModel.setTemplateId(template.getTemplateId());
                LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
                hashMap.put("value", warn.getValue());
                hashMap.put("deviceSn", warn.getDeviceSn());
                hashMap.put("deviceName", template.getDeviceName());
                hashMap.put("grade", warn.getGrade().toString());
                hashMap.put("warnTime", DateUtil.format(warn.getWarnTime(), "yy-MM-dd HH:mm:ss"));
                sendModel.setTemplateVars(hashMap);
                service.send(sendModel);
            } catch (Exception e) {
                logger.error("<<<告警短信发送异常", e);
            }
        }

        List<String> emails = template.getUsers().stream()
                .filter(item -> StrUtil.isNotBlank(item.getEmail()))
                .map(item -> item.getEmail()).distinct()
                .collect(Collectors.toList());
        MessageService emailService = messageManager.getDefault(MessageType.Email.name());
        if(emailService != null && !CollectionUtil.isEmpty(emails)) {
            SendModel sendModel = new SendModel();
            sendModel.setAccept(emails);
            sendModel.setTitle(template.getTemplateTitle());

            try {
                // 使用自定义内容发送
                if(StrUtil.isNotBlank(template.getContent())) {
                    LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
                    hashMap.put("value", warn.getValue());
                    hashMap.put("deviceSn", warn.getDeviceSn());
                    hashMap.put("grade", warn.getGrade().toString());
                    hashMap.put("deviceName", template.getDeviceName());
                    hashMap.put("warnTime", DateUtil.format(warn.getWarnTime(), "yy-MM-dd HH:mm:ss"));

                    String content = StrFormatter.format(template.getContent(), hashMap);
                    sendModel.setContent(content);
                } else {
                    // 使用告警内容发送
                    sendModel.setContent(warn.getContent());
                }

                emailService.send(sendModel);
            } catch (Exception e) {
                logger.error("<<<告警邮件发送异常", e);
            }
        }
    }

    @Override
    public ListResult<IVOption> listAccepts() {
        return new ListResult<>(this.getBaseMapper().listAccepts());
    }

    @Override
    public PageResult<Page<MessageTemplate>> detailPage(Page page, MessageTemplate entity) {
        this.getBaseMapper().detailPage(page, entity);
        return new PageResult<>(page);
    }

    @Override
    public void sendWarnMessage(WarnData warn) {
        final WarnMessageTemplate template = this.getBaseMapper().getWarnTemplateByWarnTempId(warn.getWarnConfigId(), warn.getUid());
        if(template != null && CollectionUtil.isNotEmpty(template.getUsers())) {
            template.setContent(warn.getContent());
            template.setTemplateTitle(String.format("%s设备触发告警[%s]", warn.getDeviceName(), warn.getGrade()));
            doSendWarnMsg(warn, template);
        }
    }
}
