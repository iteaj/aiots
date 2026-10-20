package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.WarnRecord;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 告警记录VO（移动端）
 */
@Data
@Accessors(chain = true)
public class WarnRecordVo {

    /** 告警内容 */
    private String content;

    /** 告警时间 */
    private Date warnTime;

    /** 设备编号 */
    private String deviceSn;

    /** 告警等级名称 */
    private String gradeName;

    public static WarnRecordVo of(WarnRecord record) {
        return new WarnRecordVo()
                .setContent(record.getContent())
                .setWarnTime(record.getWarnTime())
                .setDeviceSn(record.getDeviceSn())
                .setGradeName(record.getGradeName());
    }
}
