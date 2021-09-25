package com.iteaj.code.gen;

import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.spi.admin.MenuResource;
import com.iteaj.framework.spi.admin.MenuType;
import com.iteaj.framework.spi.admin.Resource;
import com.iteaj.framework.spi.admin.UrlResource;

import java.util.Collection;

public class GenMenuResource implements MenuResource {

    private Long pid;
    private String url;
    private String name;
    private String perms;
    private MenuType type;
    private PermStatus status;
    private Collection children;

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public MenuType getType() {
        return null;
    }

    @Override
    public Object getPosition() {
        return null;
    }

    @Override
    public Object getPermType() {
        return null;
    }

    @Override
    public PermStatus getStatus() {
        return this.status;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Resource setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Collection getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(Collection children) {
        this.children = children;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public UrlResource setUrl(String url) {
        this.url = url;
        return this;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public void setStatus(PermStatus status) {
        this.status = status;
    }
}
