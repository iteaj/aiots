package com.iteaj.iboot.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.core.config.shiro.ShiroAdminToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理中心接口
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core")
public class LoginController extends BaseController {

    /**
     * 系统登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody ShiroAdminToken token) {
        try {
            SecurityUtils.getSubject().login(token);
        } catch (SecurityException e) {
            return fail(e.getMessage());
        } catch (AuthenticationException e) {
            return fail(e.getMessage());
        } catch (Exception e) {
            return fail("登录失败");
        }

        return success("登录成功");
    }

    /**
     * 系统注销
     * @return
     */
    @PostMapping("logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return success("注销成功");
    }
}
