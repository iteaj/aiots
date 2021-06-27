package com.iteaj.framework.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "izone.web")
public class FrameworkWebProperties {

    /**
     * 当前系统访问地址,不包含uri (http://www.iteaj.com)
     */
    private String domain;

    /**
     *  认证授权配置信息
     */
    private Auth auth = new Auth();

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

    public String getDomain() {
        return domain;
    }

    public FrameworkWebProperties setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getUploadRootDir() {
        return uploadRootDir;
    }

    public String getUploadRootUri() {
        return uploadRootUri;
    }

    public FrameworkWebProperties setUploadRootUri(String uploadRootUri) {
        this.uploadRootUri = uploadRootUri;
        return this;
    }

    public FrameworkWebProperties setUploadRootDir(String uploadRootDir) {
        this.uploadRootDir = uploadRootDir;
        return this;
    }

    public Auth getAuth() {
        return auth;
    }

    public FrameworkWebProperties setAuth(Auth auth) {
        this.auth = auth;
        return this;
    }

}
