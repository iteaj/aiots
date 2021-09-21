package com.iteaj.framework.autoconfigure;

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
     *  认证授权配置信息
     */
    private Auth auth = new Auth();

    /**
     * shiro配置
     */
    private Shiro shiro;

    /**
     * 文件上传的跟目录
     * @see #uploadRootUri 此uri将映射到此目录
     * 注：只有 uploadRootDir 和 uploadRootDir 都不为空才会生效
     */
    private String uploadRootDir;
    /**
     * 上传的文件访问的路径前缀
     * @see #uploadRootDir 此访问路径将映射到此目录
     */
    private String uploadRootUri;

    public static class Auth extends AuthConfig { }

    @Data
    public static class Shiro {

        /**
         * 启用redis支持(集群)
         */
        private boolean redis;
    }
}
