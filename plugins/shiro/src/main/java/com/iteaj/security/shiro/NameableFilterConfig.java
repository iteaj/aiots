package com.iteaj.security.shiro;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * create time: 2020/3/29
 *
 * @author iteaj
 * @since 1.0
 */
public class NameableFilterConfig implements FilterConfig {

    private String name;
    private ServletContext servletContext;

    public NameableFilterConfig(String name, ServletContext servletContext) {
        this.name = name;
        this.servletContext = servletContext;
    }

    @Override
    public String getFilterName() {
        return this.name;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return null;
    }
}
