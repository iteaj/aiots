package com.iteaj.iboot.plugin.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.iteaj.framework.Entity;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.security.*;
import com.iteaj.framework.security.SecurityException;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

public class ShiroSecurityService implements SecurityService {

    private final FrameworkProperties properties;
    private final AuthorizationService authorizationService;

    public ShiroSecurityService(FrameworkProperties properties
            , AuthorizationService authorizationService) {
        this.properties = properties;
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean isLogin() {
        return SecurityUtils.getSubject().getPrincipal() != null;
    }

    public Optional<Entity> getUser() {
        return Optional.ofNullable((Entity) SecurityUtils.getSubject().getPrincipal());
    }

    @Override
    public Optional<List<String>> getRoles() {
        return getLoginId().map(item -> authorizationService.getRoles(item));
    }

    @Override
    public boolean hasRole(String role) {
        return SecurityUtils.getSubject().hasRole(role);
    }

    @Override
    public boolean hasRole(Logical logical, String... roles) {
        if(logical == Logical.AND) {
            return SecurityUtils.getSubject().hasAllRoles(Arrays.asList(roles));
        } else {
            for(boolean item : SecurityUtils.getSubject().hasRoles(Arrays.asList(roles))) {
                if(item) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }

    @Override
    public boolean hasPermission(Logical logical, String... permissions) {
        if(logical == Logical.AND) {
            return SecurityUtils.getSubject().isPermittedAll(permissions);
        } else {
            boolean[] permitted = SecurityUtils.getSubject().isPermitted(permissions);
            for(boolean item : permitted) {
                if(item) {
                    return true;
                }
            }

            return false;
        }
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

            UserAgent agent = (UserAgent)request.getAttribute(CoreConst.WEB_USER_AGENT);
            if(agent != null) {
                FrameworkProperties.Web web = properties.getWeb();
                String tokenName = web.getAuth().getTokenName();
                response.setHeader(tokenName);
            }
        } catch (AuthenticationException e) {
            throw new SecurityException(e.getMessage(), e);
        }
    }
}
