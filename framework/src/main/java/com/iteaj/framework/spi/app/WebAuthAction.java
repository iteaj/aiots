package com.iteaj.framework.spi.app;

import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.util.JsonUtils;
import com.iteaj.util.Result;
import org.springframework.core.Ordered;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * create time: 2021/3/24
 *  授权动作
 * @see WebAuthHandler
 * @author iteaj
 * @since 1.0
 */
public interface WebAuthAction<T> extends Ordered {

    @Override
    default int getOrder() {
        return 88;
    }

    /**
     * 每个授权动作的名称不能重复
     * @see WebAuthHandler#getAction(String) 通过名称获取一个动作
     * @return
     */
    default String getName() {
        return getClass().getName();
    }

    /**
     * 是否是ajax请求
     * @param request
     * @return
     */
    default boolean isAjax(HttpServletRequest request) {
        String ajaxId = request.getHeader("x-requested-with");
        return "XMLHttpRequest".equalsIgnoreCase(ajaxId);
    }

    boolean isLoginRequest(String requestUri, HttpServletRequest request);

    /**
     * 响应结果到客户端
     * @param result
     * @param response
     */
    default void writeJson(Result result, HttpServletResponse response) {
        response.setContentType("application/json;charset=utf-8");
        try(ServletOutputStream outputStream = response.getOutputStream()) {
            String toJson = JsonUtils.toJson(result);
            outputStream.write(toJson.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取登录地址
     * 登录的页面地址(GET)和提交表单登录地址(POST)必须相同
     * @return
     */
    default String getLoginUrl() {return null;}

    /**
     * 登录成功时跳转的url地址
     * @return
     */
    default String successUrl() { return null;}

    /**
     * 将符合此url模式的请求交由此action处理
     * @see org.springframework.util.PathMatcher#match(String, String)
     * 不包含上下文路径
     * @return
     */
    String[] getUrlPattern();

    /**
     * 用来处理未授权用户的请求
     * @see WebAuthHandler#handle(HttpServletRequest, HttpServletResponse)
     * @param request 授权是的请求对象
     * @param response 授权是的响应对象
     * @return AuthContext 如果返回{@code null}直接返回
     */
    AuthContext authorize(HttpServletRequest request, HttpServletResponse response) throws Exception;

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
