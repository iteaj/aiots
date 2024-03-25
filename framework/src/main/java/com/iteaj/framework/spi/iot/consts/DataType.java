package com.iteaj.framework.spi.iot.consts;

public enum DataType {

    t_any("any", "any(任何)"),
    t_byte("byte", "byte(字节)"),
    t_short("short", "short(短整型)"),
    t_int("int", "int(整型)"),
    t_long("long", "long(长整型)"),
    t_float("float", "float(单精度)"),
    t_double("double", "double(双精度)"),
    t_date("date", "date(日期)"),
    t_time("time", "time(时间)"),
    t_datetime("datetime", "datetime(完整时间)"),
    t_timestamp("timestamp", "timestamp(时间戳)"),
    t_string("string", "string(字符串)"),
    t_boolean("boolean", "boolean(布尔)"),
    ;

    private String value;
    private String desc;

    DataType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
