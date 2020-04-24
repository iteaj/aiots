package com.iteaj.admin.entity;

import com.iteaj.admin.enums.OrgType;
import com.iteaj.admin.enums.Status;
import com.iteaj.framework.TreeEntity;

/**
 * create time: 2019/11/26
 *  组织机构实体
 * @author iteaj
 * @since 1.0
 */
public class Organize extends TreeEntity {

    private String name;
    private Integer sort;
    private String phone;
    private String email;
    private String leader;
    private Integer level;
    private Status status;
    private OrgType type;

    public Integer getLevel() {
        return level;
    }

    public Organize setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public String getName() {
        return name;
    }

    public Organize setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getSort() {
        return sort;
    }

    public Organize setSort(Integer sort) {
        this.sort = sort;
        return this;
    }

    public String getLeader() {
        return leader;
    }

    public Organize setLeader(String leader) {
        this.leader = leader;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Organize setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Organize setEmail(String email) {
        this.email = email;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Organize setStatus(Status status) {
        this.status = status;
        return this;
    }

    public OrgType getType() {
        return type;
    }

    public Organize setType(OrgType type) {
        this.type = type;
        return this;
    }
}
