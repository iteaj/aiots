package com.iteaj.iboot.module.iot.dto;

import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 关联点位信息（告警时刻同一采集时间下关联的其他点位数据）
 */
@Data
@Accessors(chain = true)
public class AssociatedStats {

    /** 卡片唯一标识(uid:fieldId:采集时间戳) */
    private String id;

    /** 设备标识 */
    private String uid;

    /** 属性ID */
    private Long fieldId;

    /** 属性 */
    private String field;

    /** 属性名称 */
    private String fieldName;

    /** 设备名称 */
    private String deviceName;

    /** 设备编号 */
    private String deviceSn;

    /** 设备分组ID */
    private Long deviceGroupId;

    /** 设备分组名称 */
    private String deviceGroupName;

    /** 告警等级 */
    private String grade;

    /** 指标 */
    private String indicator;

    /** 采集值 */
    private String value;

    /** 采集时间 */
    private Date collectTime;

    public static AssociatedStats of(RealtimeCollectData d) {
        AssociatedStats a = new AssociatedStats();
        long collectTime = d.getCollectTime() != null ? d.getCollectTime().getTime() : 0;
        a.setId(d.getUid() + ":" + d.getFieldId() + ":" + collectTime);
        a.setUid(d.getUid());
        a.setField(d.getField());
        a.setFieldId(d.getFieldId());
        a.setFieldName(d.getFieldName());
        a.setDeviceName(d.getDeviceName());
        a.setDeviceSn(d.getDeviceSn());
        a.setDeviceGroupId(d.getDeviceGroupId());
        a.setValue(d.getValue() != null ? d.getValue().toString() : null);
        a.setGrade(d.getGrade());
        a.setIndicator(d.getIndicator());
        a.setCollectTime(d.getCollectTime());
        return a;
    }
}
