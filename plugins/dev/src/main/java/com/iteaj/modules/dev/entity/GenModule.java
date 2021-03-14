package com.iteaj.modules.dev.entity;

import com.iteaj.framework.BaseEntity;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
public class GenModule extends BaseEntity {

    private String msn;
    private String name;
    private String remark;
    private String project;
    private String account;
    private String dbName;
    private String password;
    private String tablePrefix;

    public String getMsn() {
        return msn;
    }

    public GenModule setMsn(String msn) {
        this.msn = msn;
        return this;
    }

    public String getName() {
        return name;
    }

    public GenModule setName(String name) {
        this.name = name;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public GenModule setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getProject() {
        return project;
    }

    public GenModule setProject(String project) {
        this.project = project;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public GenModule setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public GenModule setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public GenModule setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    public GenModule setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }
}
