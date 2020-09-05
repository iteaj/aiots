package com.iteaj.security.shiro;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.spi.admin.LoggerType;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.HttpResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.subject.WebSubject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * create time: 2019/12/6
 *  框架权限认证服务
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping
public class ShiroAdminAuthenticationController extends BaseController {

    private final AuthzUrl authzUrl;
    private final EventPublisher eventPublisher;
    private final AuthenticatingService authenticatingService;

    public ShiroAdminAuthenticationController(EventPublisher eventPublisher
            , AuthenticatingService authenticatingService, AuthzUrl authzUrl) {
        this.eventPublisher = eventPublisher;
        this.authenticatingService = authenticatingService;
        this.authzUrl = authzUrl;
    }

    /**
     * 表单提交登录功能
     * @param token
     * @return
     */
    @PostMapping("/login")
    public Result<AuthzUrl> login(@RequestBody ShiroAuthToken token) {
        try {
            // 登录
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            logger.error(e.getMessage(), e);
            return HttpResult.Fail(e.getMessage());
        }  catch (Exception e) {
            logger.error(e.getMessage(), e);
            return HttpResult.Fail("认证失败");
        }

        return HttpResult.Success(authzUrl, "认证成功");
    }

    /**
     * 注销
     */
    @RequestMapping("/logout")
    public Result logout() {
        String msg = "注销成功";
        WebSubject subject = (WebSubject) org.apache.shiro.SecurityUtils.getSubject();
        Object admin = subject.getPrincipal();
        long start = System.currentTimeMillis();
        try {
            subject.logout();
        } catch (Exception e) {
            msg = "注销失败";
            logger.error(e.getMessage(), e);
            return HttpResult.Fail("注销失败");
        } finally {
            long time = System.currentTimeMillis() - start;

            ShiroAuthorEvent event = new ShiroAuthorEvent(admin
                    , (ShiroHttpServletRequest)subject.getServletRequest()
                    , LoggerType.Logout).setMethod(null).setErrMsg(msg)
                    .setOperaTimeMillis(time).setArgs(new Object[]{admin});
            eventPublisher.publish(event);
        }
        return HttpResult.Success(authzUrl, "注销成功");
    }

}
