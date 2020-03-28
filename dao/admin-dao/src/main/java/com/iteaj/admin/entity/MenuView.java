package com.iteaj.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.admin.enums.Status;
import com.iteaj.admin.enums.ViewType;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.authority.TreeResource;
import com.iteaj.util.CommonUtils;
import org.apache.ibatis.type.ArrayTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 视图实体
 */
public class MenuView extends BaseEntity implements TreeResource {

    /**视图名称*/
    private String name;
    /**排序*/
    private Integer sort;
    /**图标*/
    private String icon;
    /**是否显示*/
    private Status status;
    /**视图类型*/
    private ViewType type;

    /**此视图下挂载的菜单列表*/
    @TableField(whereStrategy = FieldStrategy.NEVER)
    private int[] menus = new int[0];
    /**视图拥有的菜单列表*/
    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();

    public MenuView() { }

    public MenuView(Status status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ViewType getType() {
        return type;
    }

    public void setType(ViewType type) {
        this.type = type;
    }

    public int[] getMenus() {
        return menus;
    }

    public void setMenus(int[] menus) {
        this.menus = menus;
    }

    @Override
    public Collection getChildren() {
        return children;
    }

    @Override
    public void setChildren(Collection children) {
        if(!CommonUtils.isNotEmpty(children))
            return;

        this.children = (List<Menu>) children;
    }
}
