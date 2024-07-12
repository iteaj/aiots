package com.iteaj.iboot.plugin.message.service.impl;

import com.iteaj.framework.spi.message.SendTemplate;
import com.iteaj.iboot.plugin.message.entity.MessageTemplate;
import com.iteaj.iboot.plugin.message.mapper.MessageTemplateMapper;
import com.iteaj.iboot.plugin.message.service.IMessageTemplateService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;

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

    @Override
    public SendTemplate getTemplateById(Serializable key) {
        return this.getBaseMapper().getTemplateById(key);
    }
}
