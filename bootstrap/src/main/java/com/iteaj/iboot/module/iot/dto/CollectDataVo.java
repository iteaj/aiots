package com.iteaj.iboot.module.iot.dto;

import lombok.Data;

import java.util.Date;

/**
 * 移动端采集数据VO
 */
@Data
public class CollectDataVo {

    /** 属性字段 */
    private String field;

    /** 字段名称 */
    private String fieldName;

    /** 采集值 */
    private String value;

    /** 单位 */
    private String unit;

    /** 采集时间 */
    private Date collectTime;
}
