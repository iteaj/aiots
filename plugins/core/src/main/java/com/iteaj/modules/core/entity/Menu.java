package com.iteaj.modules.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.modules.core.enums.MenuType;
import com.iteaj.framework.TreeEntity;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.consts.Position;
import com.iteaj.framework.spi.admin.MenuResource;

public class Menu extends TreeEntity implements MenuResource {

    /**菜单地址*/
    private String url;
    /**菜单名称*/
    @TableField(condition = SqlCondition.LIKE)
    private String name;
    /**所属模块的模块编码*/
    private String msn;
    /**排序*/
    private Integer sort;
    /*是否记录日志*/
    private Boolean log;
    /*操作日志描述*/
    private String logDesc;
    /**图标*/
    private String icon;
    /**是否显示*/
    private PermStatus status;
    /**权限列表, Shiro使用的格式*/
    private String perms;
    /**备注*/
    private String remark;
    /**菜单类型*/
    private MenuType type;
    /**权限类型*/
    private String permType;
    /**功能点放置的位置*/
    @TableField(fill = FieldFill.UPDATE)
    private Position position;

    public Menu() {  }

    public Menu(Long pid, String url, String name, String msn
            , Integer sort, String permType, String perms, Position position) {
        setPid(pid);
        this.url = url;
        this.name = name;
        this.msn = msn;
        this.sort = sort;
        this.perms = perms;
        this.permType = permType;
        this.position = position;
    }

    public static Menu AuthMenu(Long pid, String url, String name
            , String msn, String permType, String perms, Position position) {
        return new Menu(pid, url, name, msn, null
                , permType, perms, position).setType(MenuType.A);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getMsn() {
        return msn;
    }

    public Menu setMsn(String msn) {
        this.msn = msn;
        return this;
    }

    public Integer getSort() {
        return sort;
    }

    public Menu setSort(Integer sort) {
        this.sort = sort;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public Menu setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public PermStatus getStatus() {
        return status;
    }

    public Menu setStatus(PermStatus status) {
        this.status = status;
        return this;
    }

    public String getPerms() {
        return perms;
    }

    public Menu setPerms(String perms) {
        this.perms = perms;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Menu setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public MenuType getType() {
        return type;
    }

    public Menu setType(MenuType type) {
        this.type = type;
        return this;
    }

    public String getPermType() {
        return permType;
    }

    public Menu setPermType(String permType) {
        this.permType = permType;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public Menu setPosition(Position position) {
        this.position = position;
        return this;
    }

    public Boolean getLog() {
        return log;
    }

    public Menu setLog(Boolean log) {
        this.log = log;
        return this;
    }

    public String getLogDesc() {
        return logDesc;
    }

    public Menu setLogDesc(String logDesc) {
        this.logDesc = logDesc;
        return this;
    }
}
