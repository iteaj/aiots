package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 移动端设备统计VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppDeviceStatisticsVo {

    /** 设备总数 */
    private Integer total;

    /** 在线数 */
    private Integer online;

    /** 离线数 */
    private Integer offline;
}
