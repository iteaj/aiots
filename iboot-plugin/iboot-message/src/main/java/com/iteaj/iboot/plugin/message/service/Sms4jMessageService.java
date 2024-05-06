package com.iteaj.iboot.plugin.message.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.message.MessageConfig;
import com.iteaj.framework.spi.message.SendModel;
import com.iteaj.framework.spi.message.sms.SmsMessageService;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.api.universal.SupplierConfig;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.provider.enumerate.SupplierType;

import java.io.IOException;
import java.util.*;

/**
 * 使用Sms4j框架实现的短信服务
 */
public abstract class Sms4jMessageService extends SmsMessageService {

    private Map<String, SmsBlend> smsBlendMap = new HashMap();

    public Sms4jMessageService() { }

    @Override
    public Optional<Object> send(MessageConfig config, SendModel model) {
        if(StrUtil.isBlank(model.getAccept())) {
            throw new ServiceException("接收人[accept]不能为空");
        }

        if(model.getTemplateId() == null) {
            if (Objects.isNull(model.getContent()) || StrUtil.isBlank(model.getContent().toString())) {
                throw new ServiceException("发送内容[content]不能为空");
            }
        }

        try {
            // 创建短信配置
            SupplierType supplierType = SupplierType.valueOf(config.getChannelId());
            Class<? extends SupplierConfig> aClass = supplierType.getProviderFactory().getConfig().getClass();
            JsonParser traverse = config.getConfig().traverse();
            SupplierConfig supplierConfig = mapper.readValue(traverse, aClass);

            SmsBlend smsBlend = smsBlendMap.get(getKeyId(config));
            if(smsBlend == null) {
                synchronized (this) {
                    smsBlend = smsBlendMap.get(getKeyId(config));
                    if(smsBlend == null) {
                        smsBlend = SmsFactory.createSmsBlend(supplierType, supplierConfig);
                    }
                }
            }

            // 解析接收人
            String[] accepts = model.getAccept().split(",");
            SmsResponse smsResponse = doSend(smsBlend, Arrays.asList(accepts), model);

            // 发送失败
            if(!smsResponse.isSuccess()) {
                throw new ServiceException("发送短信失败 " + smsResponse.getMessage());
            }

            return Optional.of(smsResponse);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("不支持通道["+config.getChannelId()+"]", e);
        } catch (IOException e) {
            throw new ServiceException("发送失败["+config.getChannelId()+"]", e);
        }
    }

    /**
     * 发送短信
     * @param smsBlend
     * @param accepts
     * @param model
     * @return
     */
    protected SmsResponse doSend(SmsBlend smsBlend, List<String> accepts, SendModel model) {
        // 使用模板发送
        if(model.getTemplateId() != null) {
            return smsBlend.massTexting(accepts, model.getTemplateId()
                    , new LinkedHashMap<>(model.getTemplateName()));
        } else {
            return smsBlend.massTexting(accepts, model.getContent().toString());
        }
    }

    protected String getKeyId(MessageConfig config) {
        return config.getConfig("accessKeyId");
    }

    @Override
    public boolean remove(MessageConfig config) {
        return smsBlendMap.remove(getKeyId(config)) != null;
    }
}
