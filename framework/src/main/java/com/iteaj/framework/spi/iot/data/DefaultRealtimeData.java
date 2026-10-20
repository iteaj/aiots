package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iot.tools.annotation.IotField;
import com.iteaj.iot.tools.annotation.IotTable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.iteaj.framework.spi.iot.data.RealtimeCollectData.COLLECT_TABLE_NAME;

@Data
@IotTable(COLLECT_TABLE_NAME)
@Accessors(chain = true)
public class DefaultRealtimeData implements RealtimeCollectData {

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
    private Object value;

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
    @IotField("collect_mode")
    private String collectMode;

    /**
     * 采集时间
     */
    @IotField("collect_time")
    private Date collectTime;

    /**
     * 创建时间
     */
    @IotField("create_time")
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

    /**
     * 父设备uid
     */
    @IotField
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

    public static DefaultRealtimeData of(SignalOrFieldValue item, RealtimeDeviceStatus device
            , Long taskId, String cid, Date createTime, String fieldName) {
        return new DefaultRealtimeData()
                .setFieldId(item.getId())
                .setField(item.getSignalOrField())
                .setCollectTime(item.getCollectTime())
                .setAddress(item.getAddress())
                .setCollectMode("model").setTaskId(taskId)
                .setDeviceGroupId(device.getDeviceGroupId())
                .setProductCode(device.getProductCode())
                .setProtocolCode(device.getProtocolCode())
                .setStatus(item.getStatus() == CollectStatus.Success)
                .setUid(device.getUid()).setPuid(device.getPuid()).setCid(cid)
                .setDeviceSn(device.getDeviceSn()).setReason(item.getStatus().reason)
                .setDeviceName(device.getDeviceName()).setFieldName(fieldName)
                .setCreateTime(item.getCollectTime()).setGrade(item.getGrade())
                .setValue(item.getValue()).setIndicator(item.getIndicator()).setCreateTime(createTime);
    }
}
