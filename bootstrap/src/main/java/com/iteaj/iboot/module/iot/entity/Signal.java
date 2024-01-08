package com.iteaj.iboot.module.iot.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

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
    @Excel(name = "点位名称", isImportField = "true")
    private String name;

    /**
     * 点位地址
     */
    @Excel(name = "点位地址", isImportField = "true")
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
    @Excel(name = "所属型号", isImportField = "true")
    private Integer modelId;

    /**
     * 点位字段名称
     */
    @Excel(name = "字段名称", isImportField = "true")
    private String fieldName;

    /**
     * 浮点数精度
     */
    @Excel(name = "精度", isImportField = "true", replace = {"1位_1", "2位_2", "3位_3", "4位_4", "5位_5", "6位_6"})
    private Integer accuracy;

    /**
     * 字段类型(字典 iot_field_type)
     * @see com.iteaj.iboot.module.iot.consts.IotConsts#FIELD_TYPE_INT
     * @see com.iteaj.iboot.module.iot.consts.IotConsts#FIELD_TYPE_BYTE
     * @see com.iteaj.iboot.module.iot.consts.IotConsts#FIELD_TYPE_BOOLEAN
     * ...
     */
    @Excel(name = "字段类型", isImportField = "true", dict = "iot_field_type")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer fieldType;

    private Date createTime;

    public Signal() { }

    public Signal(Integer modelId) {
        this.modelId = modelId;
    }

}
