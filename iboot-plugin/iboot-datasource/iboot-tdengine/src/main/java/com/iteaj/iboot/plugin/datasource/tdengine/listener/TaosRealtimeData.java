package com.iteaj.iboot.plugin.datasource.tdengine.listener;

import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.iot.taos.STable;
import com.iteaj.iot.tools.annotation.IotField;
import com.iteaj.iot.tools.annotation.IotTableId;
import com.iteaj.iot.tools.annotation.IotTag;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.iteaj.framework.spi.iot.data.RealtimeCollectData.COLLECT_TABLE_NAME;

@Data
@STable(value = COLLECT_TABLE_NAME, table = "iot_data_${uid}", using = true)
@Accessors(chain = true)
public class TaosRealtimeData implements RealtimeCollectData {

    /**
     * 任务执行标识
     */
    @IotField
    private String cid;

    /**
     * 设备uid
     */
    @IotTag
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
    @IotField("collect_value")
    private String value;

    /**
     * 采集状态
     */
    @IotField("collect_status")
    private Boolean status;

    /**
     * 采集状态说明
     */
    @IotField
    private String reason;

    /**
     * 采集方式
     */
    @IotField("collect_mode")
    private String collectMode;

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
     * 设备名称
     */
    @IotField("device_name")
    private String deviceName;

    /**
     * 设备编号
     */
    @IotField("device_sn")
    private String deviceSn;

    /**
     * 字段名称
     */
    @IotField("field_name")
    private String fieldName;

    /**
     * 所属设备组
     */
    @IotTag("device_group_id")
    private Long deviceGroupId;

    /**
     * 协议码
     */
    @IotTag("protocol_code")
    private String protocolCode;

    /**
     * 产品码
     */
    @IotTag("product_code")
    private String productCode;


}
