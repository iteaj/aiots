package com.iteaj.iboot.plugin.wechat.app;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.Entity;
import com.iteaj.framework.security.AuthenticationService;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.spi.admin.auth.AuthenticationUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 移动端登录接口
 * @author iteaj
 */
@RestController
@RequestMapping("/m")
public class AppLoginController extends BaseController {

    private final AuthenticationService authenticationService;

    public AppLoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * 账号密码登录
     * @param body 包含 account, password
     */
    @PostMapping("login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String account = body.get("account");
        String password = body.get("password");

        if (account == null || password == null) {
            return fail("账号或密码不能为空");
        }

        try {
            AuthenticationUser user = authenticationService.getByAccount(account);
            if (user == null) {
                return fail("账号不存在");
            }

            if (!user.allowLogin()) {
                return fail("账号已被禁用");
            }

            String md5 = SecureUtil.md5(password);
            if (!md5.equals(user.getPassword())) {
                return fail("密码不匹配");
            }

            StpUtil.login(((Entity<Long>) user).getId());

            String tokenValue = StpUtil.getTokenValue();
            long tokenTimeout = StpUtil.getTokenTimeout();

            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("token", tokenValue);
            tokenInfo.put("expire", tokenTimeout);
            tokenInfo.put("refreshToken", tokenValue);
            tokenInfo.put("refreshExpire", tokenTimeout);
            tokenInfo.put("tokenName", StpUtil.getTokenName());

            return success(tokenInfo, "登录成功");
        } catch (SecurityException e) {
            return fail(e.getMessage());
        } catch (Exception e) {
            return fail("登录失败：" + e.getMessage());
        }
    }
}
