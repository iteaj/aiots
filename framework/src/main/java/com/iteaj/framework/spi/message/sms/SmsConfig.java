package com.iteaj.framework.spi.message.sms;

import cn.hutool.json.JSONObject;
import com.iteaj.framework.spi.message.MessageConfig;
import com.iteaj.framework.spi.message.MessageType;

public class SmsConfig implements MessageConfig {

    private String type;

    private String channelId;

    private JSONObject config;

    protected SmsConfig() { }

    public SmsConfig(String channelId, JSONObject config) {
        this.config = config;
        this.channelId = channelId;
        this.type = MessageType.Sms.name();
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getChannelId() {
        return this.channelId;
    }

    @Override
    public JSONObject getConfig() {
        return this.config;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }
}
