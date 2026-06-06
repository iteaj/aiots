package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 实时数据缓存
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_realtime_cache")
public class DeviceRealtimeCache extends BaseEntity {

    /** 设备uid */
    private String uid;

    /** 模型属性id */
    private Long fieldId;

    /** 模型属性字段 */
    private String field;

    /** 值 */
    private String value;

    /** 采集时间 */
    private Date collectTime;
}
