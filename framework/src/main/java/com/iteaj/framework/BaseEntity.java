package com.iteaj.framework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.authority.tenant.TenantHandlerMethodArgumentResolver;
import com.iteaj.framework.spring.boot.config.FrameworkConfiguration;
import com.iteaj.framework.authority.tenant.Tenant;
import com.iteaj.util.module.mvc.orm.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public abstract class BaseEntity implements Entity<Long> {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField(exist = false)
    private Object[] section;
    /**
     * @see TenantHandlerMethodArgumentResolver
     * @see FrameworkConfiguration#paginationInterceptor()
     */
    @TableField(exist = false)
    private Tenant tenant;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public BaseEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public BaseEntity setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public BaseEntity setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public Object[] getSection() {
        return section;
    }

    public BaseEntity setSection(Object[] section) {
        this.section = section;
        return this;
    }
}
