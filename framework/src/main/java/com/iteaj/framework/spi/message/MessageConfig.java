package com.iteaj.framework.spi.message;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

@Data
public class MessageConfig {

    private String type;

    private String channelId;

    private ObjectNode config;

    protected MessageConfig() { }

    public MessageConfig(String type, String channelId, ObjectNode config) {
        this.type = type;
        this.config = config;
        this.channelId = channelId;
        this.type = MessageType.Sms.name();
    }

    public String getConfig(String key) {
        return this.config.get(key).asText();
    }
}
