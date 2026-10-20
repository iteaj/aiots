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
    @IotField(value = "collect_time", type = 93)
    private Date collectTime;

    /**
     * 创建时间
     */
    @IotTableId(value = "create_time")
    private Long createTime;

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

    /**
     * 父设备uid
     */
    @IotTag
    private String puid;

    /**
     * 告警等级
     */
    @IotField
    private String grade;

    /**
     * 指标
     */
    @IotField
    private String indicator;

    /**
     * 从实时采集数据构建TDengine实时数据实体
     * @param data 实时采集数据
     * @param createTime 创建时间(毫秒时间戳, 作为表主键需要唯一)
     */
    public static TaosRealtimeData of(RealtimeCollectData data, long createTime) {
        String collectValue = data.getValue() != null ? data.getValue().toString() : null;
        return new TaosRealtimeData().setAddress(data.getAddress())
                .setTaskId(data.getTaskId()).setField(data.getField())
                .setCollectTime(data.getCollectTime()).setCreateTime(createTime)
                .setStatus(data.getStatus()).setValue(collectValue)
                .setGrade(data.getGrade()).setIndicator(data.getIndicator())
                .setDeviceSn(data.getDeviceSn()).setDeviceGroupId(data.getDeviceGroupId())
                .setDeviceName(data.getDeviceName()).setFieldName(data.getFieldName())
                .setReason(data.getReason()).setCollectMode(data.getCollectMode())
                .setProductCode(data.getProductCode()).setProtocolCode(data.getProtocolCode())
                .setCid(data.getCid()).setUid(data.getUid()).setPuid(data.getPuid()).setFieldId(data.getFieldId());
    }
}
