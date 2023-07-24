package com.iteaj.iboot.plugin.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.iteaj.framework.Entity;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.security.SecurityService;
import com.iteaj.framework.security.SecurityToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShiroSecurityService implements SecurityService {

    public Optional<Entity> getUser() {
        return Optional.ofNullable((Entity) SecurityUtils.getSubject().getPrincipal());
    }

    @Override
    public Collection<String> getSessionKeys() {
        Collection<Object> keys = SecurityUtils.getSubject().getSession().getAttributeKeys();
        return keys != null ? keys.stream().map(key -> (String) key).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    @Override
    public <T> Optional<T> getSessionAttr(String key) {
        Session session = SecurityUtils.getSubject().getSession();
        return Optional.ofNullable((T) session.getAttribute(key));
    }

    @Override
    public SecurityService setSessionAttr(String key, Object value) {
        SecurityUtils.getSubject().getSession().setAttribute(key, value);
        return this;
    }

    @Override
    public Object removeSessionAttr(String key) {
        Session session = SecurityUtils.getSubject().getSession();
        return session.removeAttribute(key);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            SecurityUtils.getSubject().logout();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void login(SecurityToken token, HttpServletRequest request, HttpServletResponse response) {
        ShiroSecurityToken securityToken = new ShiroSecurityToken();
        BeanUtil.copyProperties(token, securityToken);

        try {
            SecurityUtils.getSubject().login(securityToken);
        } catch (AuthenticationException e) {
            throw new SecurityException(e.getMessage(), e);
        }
    }
}
