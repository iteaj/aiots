package com.iteaj.iboot.module.iot.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@TableName("iot_collect_detail")
public class CollectDetail extends BaseEntity {

    /**
     * 设备uid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long uid;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 存储设备uid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storeUid;

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
     * 点位组
     */
    private Long pointGroupId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子设备名称
     */
    @TableField(exist = false)
    private String childName;

    /**
     * 点位组名称
     */
    @TableField(exist = false)
    private String pointGroupName;

    /**
     * 任务名称
     */
    @TableField(exist = false)
    private String collectTaskName;

    /**
     * 设备类型
     */
    @TableField(exist = false)
    private Long deviceTypeId;

    /**
     * 型号
     */
    @TableField(exist = false)
    private Long modelId;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String deviceName;

    /**
     * 设备编号
     */
    @TableField(exist = false)
    private String deviceSn;

    /**
     * 采集设备
     */
    @TableField(exist = false)
    private DeviceDto device;

    /**
     * 存储设备
     */
    @TableField(exist = false)
    private DeviceDto store;

    /**
     * 组点位列表
     */
    @TableField(exist = false)
    private List<Signal> signals;

    @JsonIgnore
    @TableField(exist = false)
    private Map<String, Object> params;

    public Map<String, Object> resolveConfig() {
        if(StringUtils.hasText(this.getExtend())) {
            try {
                return JSONObject.parseObject(this.getExtend());
            } catch (Exception e) {
                throw new ServiceException("配置必须是标准的json格式");
            }
        } else {
            return new HashMap<>();
        }
    }
}
