package com.iteaj.iboot.plugin.message.service;

import org.dromara.sms4j.aliyun.config.AlibabaFactory;
import org.dromara.sms4j.comm.constant.SupplierConstant;
import org.dromara.sms4j.provider.factory.BaseProviderFactory;

public class AlibabaSmsService extends Sms4jMessageService {

    @Override
    public String getChannelId() {
        return SupplierConstant.ALIBABA;
    }

    @Override
    public String getChannelName() {
        return "阿里云";
    }

    @Override
    protected BaseProviderFactory buildBaseProviderFactory() {
        return AlibabaFactory.instance();
    }
}
