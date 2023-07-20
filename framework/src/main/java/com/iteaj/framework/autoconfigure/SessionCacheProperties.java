package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.spi.auth.WebAuthAction;
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
     * 全局过期时间(秒)
     * @see WebAuthAction#expireTime() 可以指定各端(pc,webapp)的过期时间
     */
    private long expire = 30 * 60;

    /**
     * session缓存名称
     */
    private String name = "SBootFrameworkSessionCache";

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
