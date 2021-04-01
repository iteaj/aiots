package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.spi.oauth2.FrameworkOauth2Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "izone.web")
public class FrameworkProperties {

    /**
     * 后台管理端配置信息
     */
    private Admin admin;


    private Oauth2 oauth2;

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

    public static class Admin extends FrameworkWebConfiguration.AdminProperties { }

    public static class Oauth2 extends FrameworkOauth2Configuration.OAuth2Properties { }

    public String getDomain() {
        return domain;
    }

    public FrameworkProperties setDomain(String domain) {
        this.domain = domain;
        return this;
    }

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

    public Oauth2 getOauth2() {
        return oauth2;
    }

    public FrameworkProperties setOauth2(Oauth2 oauth2) {
        this.oauth2 = oauth2;
        return this;
    }
}
