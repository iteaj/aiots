package com.iteaj.iboot.module.iot.dto;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.spi.iot.consts.AttrType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BatchModelDto {

    private Long productId;

    /**
     * 属性代码
     */
    private String field;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 实际类型
     */
    private String realType;

    /**
     * 读写方式
     */
    private AttrType attrType;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 单位
     */
    private String unit;

    /**
     * 精度
     */
    private Integer accuracy;

    /**
     * 数据增益
     */
    private Integer gain;

    /**
     * 数据解析器
     */
    private String resolver;

    private String remark;

    /**
     * 协议指令
     */
    private String direct;
}
