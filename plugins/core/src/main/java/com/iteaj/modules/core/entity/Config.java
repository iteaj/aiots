package com.iteaj.modules.core.entity;

import com.iteaj.modules.core.enums.ConfigType;
import com.iteaj.framework.BaseEntity;

/**
 * create time: 2019/12/4
 *
 * @author iteaj
 * @since 1.0
 */
public class Config extends BaseEntity {

    private String name;
    private String label;
    private String value;
    private String remark;
    private ConfigType type;

    public Config() { }

    public Config(ConfigType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Config setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Config setValue(String value) {
        this.value = value;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Config setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public ConfigType getType() {
        return type;
    }

    public Config setType(ConfigType type) {
        this.type = type;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Config setLabel(String label) {
        this.label = label;
        return this;
    }
}
