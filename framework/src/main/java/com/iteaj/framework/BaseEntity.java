package com.iteaj.framework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.iteaj.framework.mybatis.tenant.Tenant;
import com.iteaj.framework.mybatis.tenant.TenantHandlerMethodArgumentResolver;
import com.iteaj.util.module.mvc.orm.Entity;

public abstract class BaseEntity implements Entity<Long> {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(exist = false)
    private Object[] section;
    /**
     * @see TenantHandlerMethodArgumentResolver
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
