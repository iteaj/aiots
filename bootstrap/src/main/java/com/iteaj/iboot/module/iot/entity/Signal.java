package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 寄存器点位
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_signal")
public class Signal extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 点位名称
     */
    private String name;

    /**
     * 点位地址
     */
    private String address;

    /**
     * 设备类型
     */
    @TableField(exist = false)
    private Long typeId;

    /**
     * 信号类型(1. 点位 2. 自定义报文)
     */
    private Integer type;

    /**
     * 读取的寄存器(点位)数量
     */
    private Integer num;

    /**
     * 自定义报文
     */
    private String message;

    /**
     * 报文编码(HEX, UTF8, ASCII)
     */
    private String encode;

    /**
     * 型号id
     */
    private Integer modelId;

    /**
     * 点位字段名称
     */
    private String fieldName;

    /**
     * 字段类型(字典 iot_field_type)
     */
    private Integer fieldType;

    private Date createTime;

    public Signal() { }

    public Signal(Integer modelId) {
        this.modelId = modelId;
    }
}
