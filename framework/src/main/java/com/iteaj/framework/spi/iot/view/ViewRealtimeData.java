package com.iteaj.framework.spi.iot.view;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
public class ViewRealtimeData {

    private String type;

    private Object value;

    private String key;

    @JSONField(serialize = false)
    private String uid;

    @JSONField(serialize = false)
    private String attr;

    @JSONField(serialize = false)
    private String productCode;

    private Date collectTime;

    public final static String Attr_PREFIX = "a:";
    public final static String WARN_PREFIX = "w:";
    public final static String STATUS_PREFIX = "s:";

    public ViewRealtimeData(String type, Object value, String key, Date collectTime) {
        this.type = type;
        this.value = value;
        this.key = key;
        this.collectTime = collectTime;
    }

    public ViewRealtimeData(String productCode, String uid, String attr, String type, Object value) {
        this.uid = uid;
        this.attr = attr;
        this.type = type;
        this.value = value;
        this.productCode = productCode;
    }

    public static ViewRealtimeData initAttr(String key, Date collectTime, Object value) {
        return new ViewRealtimeData(Attr_PREFIX, value, key, collectTime);
    }

    public static ViewRealtimeData initStatus(String key, Date updateTime, Object value) {
        return new ViewRealtimeData(STATUS_PREFIX, value, key, updateTime);
    }

    public static ViewRealtimeData initWarn(String key, Date warnTime, Object value) {
        return new ViewRealtimeData(WARN_PREFIX, value, key, warnTime);
    }

    public static ViewRealtimeData buildAttr(String productCode, String uid, String attr, Object value) {
        return new ViewRealtimeData(productCode, uid, attr, Attr_PREFIX, value);
    }

    public static ViewRealtimeData buildStatus(String productCode, String uid, Object value) {
        return new ViewRealtimeData(productCode, uid, null, STATUS_PREFIX, value);
    }

    public static ViewRealtimeData buildWarn(String productCode, String uid, String attr, Object value) {
        return new ViewRealtimeData(productCode, uid, attr, WARN_PREFIX, value);
    }
}
