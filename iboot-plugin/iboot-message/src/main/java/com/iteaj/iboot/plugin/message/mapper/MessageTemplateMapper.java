package com.iteaj.iboot.plugin.message.mapper;

import com.iteaj.framework.spi.message.SendTemplate;
import com.iteaj.iboot.plugin.message.entity.MessageTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;

/**
 * <p>
 * 消息模板 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2024-07-09
 */
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {

    SendTemplate getTemplateById(Serializable id);
}
