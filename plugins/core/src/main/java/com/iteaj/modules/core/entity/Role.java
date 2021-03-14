package com.iteaj.modules.core.entity;

import com.iteaj.modules.core.enums.Status;
import com.iteaj.framework.BaseEntity;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
public class Role extends BaseEntity {

    private String name;
    private Integer sort;
    private Status status;
    private String remark;

    public String getName() {
        return name;
    }

    public Role setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getSort() {
        return sort;
    }

    public Role setSort(Integer sort) {
        this.sort = sort;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Role setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Role setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}
