package com.iteaj.core.auth;

import com.google.code.kaptcha.Constants;
import com.iteaj.core.config.CoreProperties;
import com.iteaj.core.entity.Admin;
import com.iteaj.core.service.IAdminService;
import com.iteaj.core.service.IRoleService;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.framework.spi.auth.*;
import com.iteaj.util.DigestUtils;
import com.iteaj.util.JsonUtils;
import com.iteaj.util.module.json.Json;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * create time: 2021/3/27
 *  后台管理端认证动作
 * @author iteaj
 * @since 1.0
 */
public class AdminAuthAction extends AccountAuthAction {

    @Autowired
    private CoreProperties properties;
    @Autowired
    protected IRoleService roleService;
    @Autowired
    protected IAdminService adminService;

    public AdminAuthAction(String... urlPattern) {
        super(urlPattern);
    }

    @Override
    public String getSuccessUrl() {
        return properties.getSuccessUrl();
    }

    @Override
    public String getLoginUrl() {
        return properties.getLoginUrl();
    }

    @Override
    public AuthToken login(AuthContext t, HttpServletRequest request, HttpServletResponse response) {
        try(ServletInputStream inputStream = request.getInputStream()) {
            final String content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            final Json builder = JsonUtils.builder(content);
            String userName = builder.getString("username");
            String password = builder.getString("password");
            String captcha = builder.getString("captcha");

            final Object attribute = request.getSession()
                    .getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if(!Objects.equals(attribute, captcha)) {
                throw new AuthException("验证码错误");
            } else {
                request.getSession().removeAttribute(Constants.KAPTCHA_SESSION_KEY);
            }

            Admin admin = adminService.getByAccount(userName);
            if(admin == null) {
                throw new AuthException("账户不存在");
            }

            // 密码校验
            String passwordDigest = admin.getPassword().toUpperCase();
            String md5Hex = DigestUtils.md5Hex(password.getBytes()).toUpperCase();

            if(!Objects.equals(passwordDigest, md5Hex)) {
                throw new AuthException("密码校验失败");
            }

            return getFrameworkAuthInfo(password, admin);
        } catch (IOException e) {
            throw new AuthException("参数错误");
        }
    }

    @NotNull
    protected FrameworkAuthInfo getFrameworkAuthInfo(String password, Admin admin) {
        // 超级管理员拥有所有的权限
        if(SecurityUtils.isSuper(admin.getId())) {
            return new FrameworkAuthInfo(admin, password);
        }

        List<Long> roleIds = this.roleService.selectByAdminId(admin.getId());
        List<String> roles = roleIds.stream().map(id ->
                String.valueOf(id)).collect(Collectors.toList());

        List<String> perms = this.adminService.selectPermsById(admin.getId());
        return new FrameworkAuthInfo(admin, password, roles, perms);
    }
}
