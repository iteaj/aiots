package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 告警等级统计
 */
@Getter
@Setter
public class GradeCountDto {

    /**
     * 告警等级（枚举名）
     */
    private String grade;

    /**
     * 告警数
     */
    private Integer count;
}
