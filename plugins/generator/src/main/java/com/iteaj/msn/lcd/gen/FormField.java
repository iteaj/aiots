package com.iteaj.msn.lcd.gen;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FormField {

    private String type;

    /**
     * 实体名
     */
    private String entity;

    /**
     * 表列名
     */
    private String columnName;

    /**
     * 属性名
     */
    private String propertyName;

    private String component;
}
