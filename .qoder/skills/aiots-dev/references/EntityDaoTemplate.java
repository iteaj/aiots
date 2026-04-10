package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.DeviceInstructHistory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 设备指令历史 DTO
 *
 * @author iteaj
 * @since 2026-04-03
 */
@Getter
@Setter
@Accessors(chain = true)
public class DeviceInstructHistoryDto extends DeviceInstructHistory {

    /** 创建时间 */
    private LocalDateTime createTime;

    // ==================== 查询条件字段 ====================

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;
}