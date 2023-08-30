package com.iteaj.iboot.plugin.message.service;

import org.dromara.sms4j.provider.enumerate.SupplierType;

public class AlibabaService extends Sms4jMessageService {

    @Override
    public String getChannelId() {
        return SupplierType.ALIBABA.name();
    }

    @Override
    public String getChannelName() {
        return SupplierType.ALIBABA.getName();
    }
}
