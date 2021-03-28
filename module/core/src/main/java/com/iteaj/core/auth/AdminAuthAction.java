package com.iteaj.core.auth;

import com.iteaj.core.entity.Admin;
import com.iteaj.core.service.IAdminService;
import com.iteaj.core.service.IRoleService;
import com.iteaj.framework.spi.auth.*;
import com.iteaj.util.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private IRoleService roleService;
    @Autowired
    private IAdminService adminService;

    public AdminAuthAction(String... urlPattern) {
        super(urlPattern);
    }

    @Override
    protected String getLoginUrl() {
        return getConfig().getAdmin().getLoginUrl();
    }

    @Override
    public AuthToken login(Void t, HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");

        Admin admin = adminService.getByAccount(userName);
        if(admin == null) {
            throw new AuthException("账户不存在");
        }

        // 密码校验
        String passwordDigest = admin.getPassword().toUpperCase();
        String md5Hex = DigestUtils.md5Hex(password.getBytes()).toUpperCase();

        if(Objects.equals(passwordDigest, md5Hex)) {
            throw new AuthException("密码校验失败");
        }

        List<Long> roleIds = this.roleService.selectByAdminId(admin.getId());
        List<String> roles = roleIds.stream().map(id ->
                String.valueOf(id)).collect(Collectors.toList());

        List<String> perms = this.adminService.selectPermsById(admin.getId());

        return new FrameworkAuthToken(admin, roles, perms);
    }

    @Override
    protected void nonAjaxLogoutCall(Exception cause, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 重定向到登录页面
        response.sendRedirect(getConfig().getAdmin().getLoginUrl());
    }
}
