package com.iteaj.core.dto;

import com.iteaj.core.entity.Admin;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
public class AdminDto extends Admin {

    /**
     * 是否是超级管理员
     */
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public AdminDto setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
        return this;
    }
}
