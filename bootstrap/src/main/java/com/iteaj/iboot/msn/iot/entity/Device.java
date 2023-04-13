package com.iteaj.iboot.msn.iot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.iboot.msn.iot.consts.DeviceStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * 设备表
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_device")
public class Device extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ip
     */
    private String ip;

    /**
     * 设备uid
     */
    private Long uid;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称必填")
    private String name;

    /**
     * 设备端口
     */
    private Integer port;

    /**
     * 所属用户
     */
    private Long userId;

    /**
     * 拓展字段
     */
    private String extend;

    /**
     * 设备型号
     */
    private Integer model;

    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 地址
     */
    private String address;

    /**
     * 设备账号
     */
    private String account;

    /**
     * 设备密码
     */
    private String password;

    /**
     * 设备状态
     */
    private DeviceStatus status;

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号必填")
    private String deviceSn;

    /**
     * 状态切换时间
     */
    private Date switchTime;

    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 设备类型
     */
    @NotNull(message = "设备类型必填")
    private Long deviceTypeId;

}
