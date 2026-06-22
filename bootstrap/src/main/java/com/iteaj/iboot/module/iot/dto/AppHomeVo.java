package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.WarnRecord;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 移动端首页统计VO
 */
@Data
@Accessors(chain = true)
public class AppHomeVo {

    /** 总设备数 */
    private Integer deviceTotal;

    /** 在线设备数 */
    private Integer deviceOnline;

    /** 离线设备数 */
    private Integer deviceOffline;

    /** 今日告警数 */
    private Integer alarmToday;

    /** 产品总数 */
    private Integer productTotal;

    /** 最新告警列表（最近5条） */
    private List<WarnRecord> latestWarns;

    /** 各产品设备统计 */
    private List<ProductStat> productStats;

    /** 各告警等级统计（未处理+处理中） */
    private List<AlarmGradeCount> alarmGradeCounts;

    @Data
    @Accessors(chain = true)
    public static class ProductStat {

        /** 产品名称 */
        private String name;

        /** 设备数量 */
        private Integer value;
    }

    @Data
    @Accessors(chain = true)
    public static class AlarmGradeCount {

        /** 告警等级名称（严重/紧急/告警/通知） */
        private String gradeName;

        /** 告警数 */
        private Integer count;
    }
}
