package com.iteaj.framework.shiro;

import org.apache.shiro.web.filter.AccessControlFilter;

public abstract class ShiroAccessFilter extends AccessControlFilter {

    @Override
    abstract public String getName();
}
