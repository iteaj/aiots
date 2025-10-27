package com.iteaj.plguin.protocol.hj212.network;

import cn.hutool.core.date.DateUtil;
import com.iteaj.iot.message.DefaultMessageBody;

import java.util.Date;
import java.util.Map;

public class HJ212MessageBody extends DefaultMessageBody {

    private final Map<String, String> data;

    public HJ212MessageBody(Map<String, String> data) {
        this.data = data;
    }

    public String getItem(String key) {
        return data != null ? data.get(key) : null;
    }

    public Date getCollectTime() {
        return data != null ? DateUtil.parse(data.get("DataTime"), "yyyyMMddHHmmss") : null;
    }

    public Map<String, String> getData() {
        return data;
    }
}
