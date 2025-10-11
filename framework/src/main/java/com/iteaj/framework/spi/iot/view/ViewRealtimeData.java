package com.iteaj.framework.spi.iot.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewRealtimeData {

    private String type;

    private Object value;

    private String key;

    @JsonIgnore
    private String uid;

    @JsonIgnore
    private String attr;

    @JsonIgnore
    private String productCode;

    private String deviceName;

    public final static String Attr_PREFIX = "a";
    public final static String STATUS_PREFIX = "s";

    public ViewRealtimeData(String productCode, String uid, String attr, String type, Object value) {
        this.uid = uid;
        this.attr = attr;
        this.type = type;
        this.value = value;
        this.productCode = productCode;
    }

    public static ViewRealtimeData buildAttr(String productCode, String uid, String attr, Object value) {
        return new ViewRealtimeData(productCode, uid, attr, Attr_PREFIX, value);
    }

    public static ViewRealtimeData buildStatus(String productCode, String uid, Object value) {
        return new ViewRealtimeData(productCode, uid, null, STATUS_PREFIX, value);
    }
}
