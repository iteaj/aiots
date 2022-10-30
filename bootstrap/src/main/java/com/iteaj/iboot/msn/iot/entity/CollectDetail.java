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
     * 采集动作
     */
    private String action;

    /**
     * 子设备
     */
    private String childSn;

    /**
     * 采集任务id
     */
    private Long collectTaskId;

    /**
     * 点位组
     */
    private Long pointGroupId;

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
