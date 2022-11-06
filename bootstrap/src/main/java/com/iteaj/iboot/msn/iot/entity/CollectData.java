package com.iteaj.iboot.msn.iot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.iot.tools.annotation.IotField;
import com.iteaj.iot.tools.annotation.IotTable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("iot_collect_data")
@IotTable("iot_collect_data")
public class CollectData extends BaseEntity {

    /**
     * 任务执行标识
     */
    @IotField
    private Long cid;

    /**
     * 设备uid
     */
    @IotField
    private Long uid;

    /**
     * 信号id
     */
    @IotField("signal_id")
    private Long signalId;

    /**
     * 值类型
     */
    @IotField("field_type")
    private Integer fieldType;

    /**
     * 采集任务标识
     */
    @IotField("collect_task_id")
    private Long collectTaskId;

    /**
     * 采集地址
     */
    @IotField
    private String address;

    /**
     * 采集值
     */
    @IotField
    private String value;

    /**
     * 采集状态
     */
    @IotField
    private Boolean status;

    /**
     * 采集状态说明
     */
    @IotField
    private String reason;

    /**
     * 采集时间
     */
    @IotField(value = "collect_time", type = 93)
    private Date collectTime;

    /**
     * 创建时间
     */
    @IotField(value = "create_time", type = 93)
    private Date createTime;

}
