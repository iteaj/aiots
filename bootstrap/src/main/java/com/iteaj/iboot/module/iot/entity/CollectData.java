package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.iboot.module.iot.consts.CollectMode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.iteaj.framework.spi.iot.data.RealtimeCollectData.COLLECT_TABLE_NAME;

@Data
@Accessors(chain = true)
@TableName(COLLECT_TABLE_NAME)
public class CollectData extends BaseEntity implements RealtimeCollectData {

    /**
     * 任务执行标识
     */
    private String cid;

    /**
     * 设备uid
     */
    private String uid;

    /**
     * 信号id
     */
    private Long fieldId;

    /**
     * 属性
     */
    private String field;

    @TableField(exist = false)
    private String fieldName;

    /**
     * 采集任务标识
     */
    private Long taskId;

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
     * 采集方式
     */
    private String collectMode;

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 采集时间
     */
    private Date collectTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 采集状态
     */
    @TableField(exist = false)
    private CollectStatus collectStatus;

    /**
     * 所属设备组
     */
    private Long deviceGroupId;

    /**
     * 协议码
     */
    private String protocolCode;

    /**
     * 产品码
     */
    private String productCode;

    public CollectData() { }

    public CollectData(CollectMode collectMode) {
        this.collectMode = collectMode.name();
    }

    public CollectData setCollectStatus(CollectStatus collectStatus) {
        this.collectStatus = collectStatus;
        if(collectStatus == CollectStatus.Success) {
            this.status = true;
        } else {
            this.status = false;
            this.reason = collectStatus.getReason();
        }

        return this;
    }
}
