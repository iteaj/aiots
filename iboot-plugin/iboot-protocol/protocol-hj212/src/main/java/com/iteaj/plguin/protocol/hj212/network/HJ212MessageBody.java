package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.message.DefaultMessageBody;

import java.util.Map;

public class HJ212MessageBody extends DefaultMessageBody {

    private final Map<HJ212Item, String> data;

    public HJ212MessageBody(Map<HJ212Item, String> data) {
        this.data = data;
    }

    public String getItem(HJ212Item key) {
        return data.get(key);
    }
}
