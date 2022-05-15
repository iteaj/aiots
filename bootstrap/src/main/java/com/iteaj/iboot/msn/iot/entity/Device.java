package com.iteaj.iboot.msn.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
     * 设备名称
     */
    private String name;

    /**
     * 设备端口
     */
    private Integer port;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 设备状态
     */
    private String status;

    /**
     * 设备编号
     */
    private String deviceSn;

    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 设备所属产品
     */
    private Long deviceTypeId;


}
