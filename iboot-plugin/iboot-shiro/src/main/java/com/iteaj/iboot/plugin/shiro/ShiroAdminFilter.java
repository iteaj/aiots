package com.iteaj.iboot.plugin.shiro;

import cn.hutool.json.JSONUtil;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.web.WebUtils;
import org.apache.shiro.web.filter.authc.UserFilter;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ShiroAdminFilter extends UserFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if(WebUtils.isAjax((HttpServletRequest) request)) {
            response.setContentType("application/json;charset=utf-8");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(JSONUtil.toJsonStr(Result.fail("未认证")));
            response.getWriter().flush();
            return false;
        }

        return super.onAccessDenied(request, response);
    }

    @Override
    protected String getName() {
        return super.getName();
    }
}
