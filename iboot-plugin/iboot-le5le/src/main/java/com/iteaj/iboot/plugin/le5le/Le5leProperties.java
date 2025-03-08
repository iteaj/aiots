package com.iteaj.iboot.plugin.le5le;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "plugin.le5le")
public class Le5leProperties {

    /**
     * 接口地址
     */
    private String apiUrl;

    /**
     * 账号地址
     */
    private String accountUrl;

    /**
     * 发布目录
     */
    private String deployDir;
}
