package com.iteaj.iboot.plugin.oauth2.sa;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.iteaj.framework.result.HttpResult;
import com.iteaj.framework.security.OpenToken;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.open.OpenApiUtil;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2App;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2AppService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class OpenApiFilter extends OncePerRequestFilter {

    private final Oauth2AppService oauth2AppService;

    public OpenApiFilter(Oauth2AppService oauth2AppService) {
        this.oauth2AppService = oauth2AppService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String clientId = request.getHeader("clientId");
        final String secret = request.getHeader("secret");
        final String timestamp = request.getHeader("timestamp");
        if(clientId == null || secret == null || timestamp == null) {
            return true;
        }

        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientId = request.getHeader("clientId");
        final String secret = request.getHeader("secret");
        final String timestamp = request.getHeader("timestamp");
        if(StrUtil.isBlank(clientId) || StrUtil.isBlank(timestamp) || StrUtil.isBlank(secret)) {
            write(response, HttpResult.Fail("参数不完整"));
            return;
        }

        Oauth2App oauth2App = this.oauth2AppService.getByClientId(clientId).getData();
        if(oauth2App == null) {
            write(response, HttpResult.Fail("无权限"));
        } else {
            if(!oauth2App.getStatus()) {
                write(response, HttpResult.Fail("此客户端已禁用"));
                return;
            }

            final String secretStr = String.format("%s&%s&%s", timestamp, clientId, oauth2App.getClientSecret());
            final String md5 = SecureUtil.md5(secretStr);
            if(Objects.equals(secret, md5)) {
                OpenApiUtil.buildOpenRequest(request);
                StpUtil.getStpLogic().setTokenValueToStorage(clientId);

                if(!SecurityUtil.isLogin()) { // 登录
                    SecurityUtil.login(new OpenToken(clientId, oauth2App.getAdminAccount()), request, response);
                }

                filterChain.doFilter(request, response);
            } else {
                write(response, HttpResult.Fail("密文校验失败"));
            }
        }
    }

    private void write(HttpServletResponse response, HttpResult result) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try(ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(JSON.toJSONBytes(result));
            outputStream.flush();
        }
    }
}
