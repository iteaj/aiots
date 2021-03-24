package com.iteaj.framework.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * create time: 2021/3/21
 *  app端相关配置
 * @author iteaj
 * @since 1.0
 */
@ConfigurationProperties(prefix = "izone.web.app")
public class FrameworkAppProperties {

    /**
     * 需要过滤拦截的url
     */
    private String[] includes = new String[]{"/app/**"};

    /**
     * 不需要过滤拦截的url
     */
    private String[] excludes;

    public String[] getIncludes() {
        return includes;
    }

    public FrameworkAppProperties setIncludes(String[] includes) {
        this.includes = includes;
        return this;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public FrameworkAppProperties setExcludes(String[] excludes) {
        this.excludes = excludes;
        return this;
    }
}
