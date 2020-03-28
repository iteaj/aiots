package com.iteaj.framework.authority;

import com.iteaj.framework.Resource;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.TreeSet;

public class ModuleResource<A extends Resource> implements Serializable {

    /**模块名称*/
    private String name;

    /**应用标识, 模块编码*/
    private String msn;

    /**应用的所有菜单*/
    private TreeSet<A> authorityResources;

    public ModuleResource(String name, String msn, TreeSet<A> authorityResources) {
        this.msn = msn;
        this.name = name;
        this.authorityResources = authorityResources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public TreeSet<A> getAuthorityResources() {
        return authorityResources;
    }

    public void setAuthorityResources(TreeSet<A> authorityResources) {
        this.authorityResources = authorityResources;

        if(CollectionUtils.isEmpty(authorityResources)) return;
    }
}
