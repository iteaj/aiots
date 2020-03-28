package com.iteaj.admin.entity;

import com.iteaj.admin.enums.Status;
import com.iteaj.framework.BaseEntity;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/7/13
 */
public class DictType extends BaseEntity {

    private String type;
    private String name;
    private Status status;
    private String remark;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
