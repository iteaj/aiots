package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.consts.DataType;
import lombok.Getter;
import lombok.Setter;

/**
 * 上行模型属性
 */
@Getter
@Setter
public class UpModelAttr {

    private Long id;

    /**
     * 属性代码
     */
    private String field;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性类型
     */
    private DataType dataType;

    /**
     * 精度
     */
    private Integer accuracy;

    public UpModelAttr(Long id, String field, String name, DataType dataType, Integer accuracy) {
        this.id = id;
        this.field = field;
        this.name = name;
        this.accuracy = accuracy;
        this.dataType = dataType;
    }
}
