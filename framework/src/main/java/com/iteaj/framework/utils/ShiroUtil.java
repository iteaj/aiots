package com.iteaj.framework.utils;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.Entity;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.ThreadContext;

import javax.servlet.http.HttpServletRequest;

public class ShiroUtil {

    /**
     * 返回当前登录的用户
     * @return
     */
    public static <T extends Entity> T getUser() {
        return (T) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 是否已经认证登录
     * @return
     */
    public static boolean isLogin() {
        return getUser() != null;
    }

    /**
     * 后台管理端：当前用户是否超级管理员
     * @return
     */
    public static boolean isSuper() {
        if(getUser().getId() instanceof Number) {
            return isSuper(((Number) getUser().getId()).longValue());
        }

        return false;
    }

    /**
     * 后台管理端：验证用户是不是超级管理员
     * @param id
     * @return
     */
    public static boolean isSuper(Long id) {
        return id == 1l;
    }

    public static UserAgent getAgent() {
        return (UserAgent) getRequest().getAttribute(CoreConst.WEB_USER_AGENT);
    }

    /**
     * 获取当前请求
     * @return
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) ThreadContext.get(CoreConst.HTTP_SERVLET_REQUEST);
    }

    public static <T> T getRequestAttr(String key) {
        return (T) getRequest().getAttribute(key);
    }

    public static void setRequestAttr(String key, Object value) {
        getRequest().setAttribute(key, value);
    }
}
