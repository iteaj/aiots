package com.iteaj.iboot.plugin.datasource.tdengine.listener;

import com.iteaj.iot.taos.STable;
import com.iteaj.iot.tools.annotation.IotField;
import com.iteaj.iot.tools.annotation.IotTableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@STable(value = "iot_data", table = "iot_data_${uid}", using = true)
@Accessors(chain = true)
public class TaosRealtimeData {

    /**
     * 任务执行标识
     */
    @IotField
    private String cid;

    /**
     * 设备uid
     */
    @IotField
    private String uid;

    /**
     * 信号id
     */
    @IotField("field_id")
    private Long fieldId;

    /**
     * 属性
     */
    @IotField
    private String field;

    /**
     * 采集任务标识
     */
    @IotField("task_id")
    private Long taskId;

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
     * 采集方式
     */
    @IotField
    private String mode;

    /**
     * 采集时间
     */
    @IotTableId(value = "collect_time", type = 93)
    private Date collectTime;

    /**
     * 创建时间
     */
    @IotField(value = "create_time", type = 93)
    private Date createTime;

    /**
     * 所属设备组
     */
    @IotField("device_group_id")
    private Long deviceGroupId;

    /**
     * 协议码
     */
    @IotField("protocol_code")
    private String protocolCode;

    /**
     * 产品码
     */
    @IotField("product_code")
    private String productCode;
}
