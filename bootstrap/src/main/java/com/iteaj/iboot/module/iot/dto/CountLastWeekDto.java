package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 最近一周数据
 */
@Getter
@Setter
public class CountLastWeekDto {

    /**
     * 值
     */
    private Integer value;

    /**
     * 时间
     */
    private String time;

    public CountLastWeekDto buildZero(String time) {
        this.time = time; value = 0; return this;
    }
}
