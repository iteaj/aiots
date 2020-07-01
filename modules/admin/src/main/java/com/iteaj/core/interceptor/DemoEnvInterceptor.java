package com.iteaj.core.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.iteaj.core.entity.Menu;
import com.iteaj.core.service.IMenuService;
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

    private IMenuService menuService;
    private ObjectMapper objectMapper;

    public DemoEnvInterceptor(ObjectMapper objectMapper, IMenuService menuService) {
        this.menuService = menuService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Menu requestMenu = menuService.getByUrl(request.getRequestURI());
        if(null == requestMenu) return true;

        String method = request.getMethod();
        // 演示环境视图类型和查询接口不拦截, 其余接口全部不能访问
        if(Objects.equal(requestMenu.getPermType(), "View")
                || Objects.equal("GET", method)) {
            return true;
        } else {

            objectMapper.writeValue(response.getOutputStream(), HttpResult.Fail("演示环境, 不允许操作"));
            return false;
        }
    }
}
