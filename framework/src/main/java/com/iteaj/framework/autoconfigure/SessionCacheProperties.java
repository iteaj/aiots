package com.iteaj.framework.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * create time: 2021/7/3
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "cache.session")
public class SessionCacheProperties {

    /**
     * 过期时间(秒)
     */
    private long expire = 30 * 60;

    /**
     * session缓存名称
     */
    private String name = "FrameworkSessionCache";

}
