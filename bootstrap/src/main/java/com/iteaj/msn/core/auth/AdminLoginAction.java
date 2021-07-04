package com.iteaj.msn.core.auth;

import com.google.code.kaptcha.Constants;
import com.iteaj.framework.shiro.FrameworkToken;
import com.iteaj.msn.core.CoreProperties;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.service.IAdminService;
import com.iteaj.msn.core.service.IRoleService;
import com.iteaj.framework.utils.ShiroUtil;
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
public class AdminLoginAction extends AccountAuthAction {

    @Autowired
    private CoreProperties properties;
    @Autowired
    protected IRoleService roleService;
    @Autowired
    protected IAdminService adminService;

    public AdminLoginAction(String... urlPattern) {
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
            String userName = builder.getString("userName");
            String password = builder.getString("password");
            String captcha = builder.getString("captcha");

            final Object attribute = request.getSession()
                    .getAttribute(Constants.KAPTCHA_SESSION_KEY);
            final Long createTime = (Long)request.getSession()
                    .getAttribute(Constants.KAPTCHA_SESSION_DATE);
            if(!Objects.equals(attribute, captcha) || createTime == null) {
                throw new AuthException("验证码错误");
            } else {

                request.getSession().removeAttribute(Constants.KAPTCHA_SESSION_KEY);
                request.getSession().removeAttribute(Constants.KAPTCHA_SESSION_DATE);
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

            return createFrameworkToken(password, admin);
        } catch (IOException e) {
            throw new AuthException("参数错误");
        }
    }

    @NotNull
    protected FrameworkToken createFrameworkToken(String password, Admin admin) {
        // 超级管理员拥有所有的权限
        if(ShiroUtil.isSuper(admin.getId())) {
            return new FrameworkToken(admin, password);
        }

        List<Long> roleIds = this.roleService.selectByAdminId(admin.getId());
        List<String> roles = roleIds.stream().map(id ->
                String.valueOf(id)).collect(Collectors.toList());

        List<String> perms = this.adminService.selectPermsById(admin.getId());
        return new FrameworkToken(admin, password, roles, perms);
    }
}
