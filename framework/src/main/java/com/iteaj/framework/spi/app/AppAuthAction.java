package com.iteaj.framework.spi.app;

import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/24
 *  授权动作
 * @see AppAuthHandler
 * @author iteaj
 * @since 1.0
 */
public interface AppAuthAction<T> extends Ordered {

    @Override
    default int getOrder() {
        return 88;
    }

    /**
     * 每个授权动作的名称不能重复
     * @see AppAuthHandler#getAction(String) 通过名称获取一个动作
     * @return
     */
    default String getName() {
        return getClass().getName();
    }

    /**
     * url匹配模式
     * 如果请求的url和此模式匹配将执行此动作
     * @see org.springframework.util.PathMatcher#match(String, String)
     * @return
     */
    String getUrlPattern();

    /**
     * 授权动作在未认证的时候调用
     * 请求的url匹配之后将执行此方法
     * @param request 授权是的请求对象
     * @param response 授权是的响应对象
     * @return AuthContext
     */
    AuthContext authorize(HttpServletRequest request, HttpServletResponse response);

    /**
     * 注销
     * @param request
     * @param response
     */
    void logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 将在app端提交登录请求时调用
     * @param request
     * @param response
     */
    Object login(T t, HttpServletRequest request, HttpServletResponse response);
}
