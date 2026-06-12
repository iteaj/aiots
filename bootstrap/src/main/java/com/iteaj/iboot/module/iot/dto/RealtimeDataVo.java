package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 移动端设备实时数据VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class RealtimeDataVo {

    /** 属性字段 */
    private String field;

    /** 属性名称 */
    private String name;

    /** 属性值 */
    private String value;

    /** 单位 */
    private String unit;

    /** 字典标签（字典属性时，value对应的标签） */
    private String label;

    /** 采集时间 */
    private String collectTime;

    public RealtimeDataVo(String field, String name, String unit) {
        this.field = field;
        this.name = name;
        this.unit = unit;
    }
}
