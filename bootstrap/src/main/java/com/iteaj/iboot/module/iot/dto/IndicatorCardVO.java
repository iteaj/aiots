package com.iteaj.iboot.module.iot.dto;

import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 数据指标卡片VO（采集历史数据的卡片展示，只保留前端需要的字段）
 */
@Data
@Accessors(chain = true)
public class IndicatorCardVO {

    /** 卡片唯一标识（uid:fieldId:采集时间戳） */
    private String id;

    /** 设备标识 */
    private String uid;

    /** 设备名称 */
    private String deviceName;

    /** 属性ID */
    private Long fieldId;

    /** 属性 */
    private String field;

    /** 属性名称 */
    private String name;

    /** 采集值 */
    private String value;

    /** 点位地址 */
    private String address;

    /** 告警等级 */
    private String grade;

    /** 指标 */
    private String indicator;

    /** 产品编码 */
    private String productCode;

    /** 采集时间 */
    private Date collectTime;

    /** 设备分组ID */
    private Long groupId;

    /** 设备分组名称 */
    private String deviceGroupName;

    /**
     * 从采集数据构建卡片VO
     * @param d 采集数据
     * @return 卡片VO
     */
    public static IndicatorCardVO of(RealtimeCollectData d) {
        IndicatorCardVO vo = new IndicatorCardVO();
        vo.setId(d.getUid() + ":" + d.getFieldId());
        vo.setUid(d.getUid());
        vo.setDeviceName(d.getDeviceName());
        vo.setFieldId(d.getFieldId());
        vo.setField(d.getField());
        vo.setGroupId(d.getDeviceGroupId());
        vo.setName(d.getFieldName());
        vo.setValue(d.getValue() != null ? d.getValue().toString() : null);
        vo.setAddress(d.getAddress());
        vo.setGrade(d.getGrade());
        vo.setIndicator(d.getIndicator());
        vo.setProductCode(d.getProductCode());
        vo.setCollectTime(d.getCollectTime());
        return vo;
    }
}
