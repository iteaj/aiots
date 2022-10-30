package com.iteaj.iboot.msn.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("iot_collect_detail")
public class CollectDetail extends BaseEntity {

    /**
     * 设备uid
     */
    private Long uid;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String deviceName;

    /**
     * 采集动作
     */
    private String collectAction;

    /**
     * 存储动作
     */
    private String storeAction;

    /**
     * 子设备
     */
    private String childSn;

    /**
     * 采集任务id
     */
    private Long collectTaskId;

    /**
     * 任务名称
     */
    @TableField(exist = false)
    private String collectTaskName;

    /**
     * 点位组
     */
    private Long pointGroupId;

    /**
     * 点位组名称
     */
    @TableField(exist = false)
    private String pointGroupName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 采集设备
     */
    @TableField(exist = false)
    private DeviceDto device;

    /**
     * 组点位列表
     */
    @TableField(exist = false)
    private List<Signal> signals;
}
