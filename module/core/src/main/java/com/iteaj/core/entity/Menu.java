package com.iteaj.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.core.enums.MenuType;
import com.iteaj.framework.TreeEntity;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.consts.Position;
import com.iteaj.framework.spi.admin.MenuResource;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel
@TableName("t_menu")
@Accessors(chain = true)
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

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

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

    public String getUrl() {
        return url;
    }

    public Menu setUrl(String url) {
        this.url = url;
        return this;
    }
}
