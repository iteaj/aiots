package com.iteaj.iboot.plugin.message.service;

import com.iteaj.framework.exception.FrameworkException;
import com.iteaj.framework.spi.message.sms.SmsConfig;
import com.iteaj.framework.spi.message.sms.SmsMessageService;
import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.universal.SupplierConfig;
import org.dromara.sms4j.comm.config.BaseConfig;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.provider.enumerate.SupplierType;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用Sms4j框架实现的短信服务
 */
public abstract class Sms4jMessageService extends SmsMessageService {

    private Map<String, SmsBlend> smsBlendMap = new HashMap();

    public Sms4jMessageService() {
    }

    @Override
    public void send(SmsConfig config, Object args) {
        try {
            SupplierType supplierType = SupplierType.valueOf(config.getChannelId());
            BaseConfig build = AlibabaConfig.builder().build();
            SmsBlend smsBlend = smsBlendMap.get(build.getAccessKeyId());
            if(smsBlend == null) {
                synchronized (this) {
                    smsBlend = smsBlendMap.get(build.getAccessKeyId());
                    if(smsBlend == null) {
                        smsBlend = SmsFactory.createSmsBlend(supplierType, (SupplierConfig) build);
                    }
                }
            }

//            smsBlend.sendMessage()
        } catch (IllegalArgumentException e) {
            throw new FrameworkException("不支持通道["+config.getChannelId()+"]");
        }
    }

}
