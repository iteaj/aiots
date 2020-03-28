package com.iteaj.admin.entity;

import com.iteaj.admin.enums.Status;
import com.iteaj.framework.BaseEntity;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
public class DictData extends BaseEntity {

    private Integer sort;
    private String label;
    private String value;
    private String type;
    private boolean izDefault;
    private Status status;
    private String remark;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIzDefault() {
        return izDefault;
    }

    public void setIzDefault(boolean izDefault) {
        this.izDefault = izDefault;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
