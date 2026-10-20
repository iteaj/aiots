package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端设备卡片VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppDeviceCardVo {

    /** 设备ID */
    private Long deviceId;

    /** 设备UID */
    private String uid;

    /** 设备编号 */
    private String deviceSn;

    /** 设备名称 */
    private String deviceName;

    /** 是否在线 */
    private Boolean online;

    /** 最新采集时间 */
    private Date collectTime;

    /** 今日告警数 */
    private Integer todayWarnCount;

    /** 实时属性值列表（卡片第二层） */
    private List<AttrValueVo> values;

    /** 控制状态命令结构 */
    private List<CtrlCommandDto> ctrlCmds;

    /** 控制值 */
    private Map<String, String> ctrlValue = new HashMap<>();

    public AppDeviceCardVo(List<CtrlCommandDto> ctrlCmds, List<AttrValueVo> values, String deviceName, String deviceSn, String uid, Boolean online) {
        this.ctrlCmds = ctrlCmds;
        this.values = values;
        this.deviceName = deviceName;
        this.deviceSn = deviceSn;
        this.uid = uid;
        this.online = online;
    }

    public void putCtrlValue(String key, String value) {
        this.ctrlValue.put(key, value);
    }

    @Getter
    @Setter
    public static class AttrValueVo {
        /** 属性字段 */
        private String field;
        /** 属性名称 */
        private String name;
        /** 属性单位 */
        private String unit = "";
        /** 属性最新值 */
        private String value = "-";
        /** 值标签（字典翻译） */
        private String label;

        public AttrValueVo(String field, String name, String unit) {
            this.field = field;
            this.name = name;
            this.unit = unit;
        }
    }
}
