package com.iteaj.framework.spi.message.sms;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.message.MessageService;
import com.iteaj.framework.spi.message.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * 短信服务
 */
public abstract class SmsMessageService implements MessageService {

    private List<ParamMeta> params = new ArrayList<>();

    public SmsMessageService() {
        this.buildConfigParams();
    }

    protected void buildConfigParams() {
        params.add(ParamMeta.buildRequired("signature", "signature"));
        params.add(ParamMeta.buildRequired("accessKeyId", "accessKeyId"));
        params.add(ParamMeta.buildRequired("accessKeySecret", "accessKeySecret"));
        params.add(ParamMeta.build("regionId", "regionId", "可选(cn-hangzhou)"));
    }


    @Override
    public List<ParamMeta> getConfigParams() {
        return params;
    }

    @Override
    public String getType() {
        return MessageType.Sms.name();
    }

    @Override
    public String getName() {
        return MessageType.Sms.getDesc();
    }

    @Override
    public abstract String getChannelId();

    @Override
    public abstract String getChannelName();
}
