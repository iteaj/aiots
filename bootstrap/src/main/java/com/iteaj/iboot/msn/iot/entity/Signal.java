package com.iteaj.iboot.msn.iot.entity;

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
     * 型号id
     */
    private Integer modelId;

    /**
     * 点位字段名称
     */
    private String fieldName;

    /**
     * 字段类型(int、float、...)
     */
    private String fieldType;

    private Date createTime;

    public Signal() { }

    public Signal(Integer modelId) {
        this.modelId = modelId;
    }
}
