package com.iteaj.iboot.msn.iot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("iot_collect_data")
public class CollectData extends BaseEntity {

    /**
     * 任务执行标识
     */
    private Long cid;

    /**
     * 设备uid
     */
    private Long uid;

    /**
     * 值类型
     */
    private Integer fieldType;

    /**
     * 采集任务标识
     */
    private Long collectTaskId;

    /**
     * 采集地址
     */
    private String address;

    /**
     * 采集值
     */
    private String value;

    /**
     * 采集状态
     */
    private Boolean status;

    /**
     * 采集状态说明
     */
    private String reason;

    /**
     * 采集时间
     */
    private Date collectTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
