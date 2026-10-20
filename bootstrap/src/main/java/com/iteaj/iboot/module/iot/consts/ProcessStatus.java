package com.iteaj.iboot.module.iot.consts;

/**
 * 告警处理状态
 */
public enum ProcessStatus {

    UNPROCESSED(0, "未处理"),
    PROCESSING(1, "处理中"),
    PROCESSED(2, "已处理"),
    IGNORED(3, "已忽略");

    private final int code;
    private final String desc;

    ProcessStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ProcessStatus of(Integer code) {
        if (code == null) return null;
        for (ProcessStatus s : values()) {
            if (s.code == code) return s;
        }
        return null;
    }
}
