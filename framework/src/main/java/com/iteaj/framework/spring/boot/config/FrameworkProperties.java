package com.iteaj.framework.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "izone.web")
public class FrameworkProperties {

    /**
     * 移动端配置
     */
    private App app;

    /**
     * 后台管理端配置信息
     */
    private Admin admin;

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

    public static class Admin extends AdminConfig{ }

    public static class App extends AppConfig { }

    public Admin getAdmin() {
        return admin;
    }

    public FrameworkProperties setAdmin(Admin admin) {
        this.admin = admin;
        return this;
    }

    public String getUploadRootDir() {
        return uploadRootDir;
    }

    public String getUploadRootUri() {
        return uploadRootUri;
    }

    public FrameworkProperties setUploadRootUri(String uploadRootUri) {
        this.uploadRootUri = uploadRootUri;
        return this;
    }

    public FrameworkProperties setUploadRootDir(String uploadRootDir) {
        this.uploadRootDir = uploadRootDir;
        return this;
    }

    public Auth getAuth() {
        return auth;
    }

    public FrameworkProperties setAuth(Auth auth) {
        this.auth = auth;
        return this;
    }

    public App getApp() {
        return app;
    }

    public FrameworkProperties setApp(App app) {
        this.app = app;
        return this;
    }
}
