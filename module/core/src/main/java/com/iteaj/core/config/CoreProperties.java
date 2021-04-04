package com.iteaj.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * create time: 2021/4/3
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "izone.web.admin")
public class CoreProperties {
    /**
     * 登录页的url(GET)和提交的url(POST)必须相同
     */
    private String loginUrl;

    /** 认证成功后要跳转的Url*/
    private String successUrl;

    /** 认证失败后要跳转的Url */
    private String unauthorizedUrl;

    /**
     * 缓存的存活时间 (默认时间30分钟)
     */
    private long timeToLiveSeconds = 30 * 60;

    /**
     * 设置对象在失效前的允许闲置时间（单位：秒）
     * 默认0 说明此属性无效(也就是可闲置时间无穷大)
     */
    private long timeToIdleSeconds = 10 * 60;

    /**
     * 是否允许缓存token(redis只缓存session)
     */
    private boolean cacheEnable;

    /**
     * 令牌(token)的缓存名称
     */
    private String tokenCacheName = "izone_token_cache";

    /** 验证码类型, 默认数字相加 e.g. 3+3*/
    private String captchaType = "math";

    /** 是否开启验证码 */
    private boolean captchaEnabled = true;
}
