package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.captcha.CaptchaType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "framework")
public class FrameworkProperties {

    /**
     * 当前系统访问地址,不包含uri (http://www.iteaj.com)
     */
    private String domain;

    /**
     * 是否启用集群模式
     */
    private boolean cluster;

    /**
     * web环境配置
     */
    private Web web = new Web();

    /**
     * 校验码配置
     */
    private CaptchaConfig captcha = new CaptchaConfig();

    /**
     * 文件上传的跟目录
     * @see #uploadPattern 此uri将映射到此目录
     * 注：只有 uploadRootDir 和 uploadRootDir 都不为空才会生效
     */
    private String uploadLocation;
    /**
     * 上传的文件访问的路径前缀
     * @see #uploadLocation 此访问路径将映射到此目录
     */
    private String uploadPattern;

    @Data
    public static class Web {

        /**
         * 启用日志记录
         */
        private boolean logger;

        private Auth auth = new Auth();

        /**
         * session配置
         */
        private Session session = new Session();
    }

    @Data
    public static class Auth {
        /**
         * 系统统一注销地址
         */
        private String logoutUri = "/auth/logout";

        /**
         * 原生app请求头的token名称
         */
        private String tokenName = "access_token";

        /**
         * 浏览器, 小程序等超时时间(秒) 默认30分钟
         */
        private long timeout = 30 * 60;

        /**
         * 原生app超时时间(秒) 默认30天
         */
        private long appTimeout = 30 * 24 * 60 * 60;
    }

    @Data
    public static class Session {

        /**
         * 过期时间(秒)
         */
        private long expire = 30 * 60;

        /**
         * 缓存名称
         */
        private String name = "IBoot:Session";
    }

    @Data
    public static class CaptchaConfig {

        /**
         * 有效期(秒)
         * 默认1分钟
         */
        private long expire = 1 * 60;

        private CaptchaType type = CaptchaType.Math;
    }
}
