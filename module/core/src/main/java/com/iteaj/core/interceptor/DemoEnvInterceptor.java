package com.iteaj.core.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.util.module.mvc.HttpResult;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2020/5/1
 *  演示环境拦截
 * @author iteaj
 * @since 1.0
 */
public class DemoEnvInterceptor implements HandlerInterceptor {

    private AuthzUrl authzUrl;
    private ObjectMapper objectMapper;

    public DemoEnvInterceptor(ObjectMapper objectMapper, AuthzUrl authzUrl) {
        this.authzUrl = authzUrl;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();

        // 演示环境视放行GET接口和登录接口, 其余接口全部不能访问
        if(Objects.equal("GET", method) || authzUrl
                .getLoginUrl().contains(request.getRequestURI())) {
            return true;
        } else {

            objectMapper.writeValue(response.getOutputStream(), HttpResult.Fail("演示环境, 不允许操作"));
            return false;
        }
    }
}
