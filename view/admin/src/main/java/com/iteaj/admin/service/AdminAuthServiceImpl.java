package com.iteaj.admin.service;

import com.iteaj.framework.authority.AuthException;
import com.iteaj.framework.authority.AuthService;
import com.iteaj.framework.authority.AuthToken;
import com.iteaj.framework.shiro.CommonAuthenticationException;
import com.iteaj.framework.shiro.ShiroTokenDecorate;
import com.iteaj.framework.utils.ShiroUtils;
import com.iteaj.util.module.mvc.ServiceException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2020/3/4
 *  管理端认证服务实现
 * @author iteaj
 * @since 1.0
 */
@Component
public class AdminAuthServiceImpl implements AuthService {
    @Override
    public String login(HttpServletRequest request, HttpServletResponse response) {
        return "login";
    }

    @Override
    public void login(AuthToken token, HttpServletRequest request, HttpServletResponse response) {
        try {
            if(null == token.getPrincipal() || null == token.getCredentials())
                throw new AuthException("账号或密码错误");

            // 登录
            SecurityUtils.getSubject().login(new ShiroTokenDecorate(token));
        } catch (UnknownAccountException e) {
            throw new ServiceException("账号不存在", e);
        } catch (CredentialsException e) {
            throw new ServiceException("账号密码不匹配", e);
        } catch (CommonAuthenticationException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (AuthenticationException e) {
            throw new ServiceException("认证失败", e);
        }
    }

    @Override
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        ShiroUtils.logout();
        return "redirect:/auth/login";
    }

    @Override
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return "index";
    }
}
