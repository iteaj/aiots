package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import com.iteaj.framework.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 设备分组
 * @author iteaj
 */
@Data
@Accessors(chain = true)
@TableName("iot_device_group")
public class DeviceGroup extends TreeEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父级
     */
    private Long pid;

    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 路径
     */
    private String path;

    /**
     * 备注
     */
    private String remark;

    /**
     * 分组地址
     */
    private String address;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 父级名称
     */
    @TableField(exist = false)
    private String parentName;

    @TableField(exist = false)
    private List<Long> deviceIds;
}
