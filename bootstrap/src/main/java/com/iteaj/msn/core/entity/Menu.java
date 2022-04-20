package com.iteaj.msn.core.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.TreeEntity;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.consts.Position;
import com.iteaj.framework.logger.LoggerMenu;
import com.iteaj.framework.spi.admin.MenuType;
import com.iteaj.msn.core.enums.Status;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 菜单实体
 */
@Data
@TableName("t_menu")
@Accessors(chain = true)
public class Menu extends TreeEntity implements LoggerMenu {

    /**菜单地址*/
    private String url;
    /**菜单名称*/
    @TableField(condition = SqlCondition.LIKE)
    private String name;
    /**排序*/
    private Integer sort;
    /**图标*/
    private String icon;
    /**是否显示*/
    private Status status;
    /**权限列表, Shiro使用的格式*/
    private String perms;
    /**备注*/
    private String remark;
    /**菜单类型*/
    private MenuType type;
    /**是否记录日志*/
    private Boolean log;
    /**日志信息*/
    private String logDesc;

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    public Menu() {  }

    public String getUrl() {
        return url;
    }

    public Menu setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public boolean isCollect() {
        return this.log == null ? false : this.log;
    }

    @Override
    public String getDesc() {
        return this.logDesc;
    }
}
