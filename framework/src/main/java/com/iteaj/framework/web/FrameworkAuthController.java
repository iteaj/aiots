package com.iteaj.framework.web;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.utils.ShiroUtils;
import com.iteaj.izone.api.LoggerType;
import com.iteaj.izone.spi.AuthorizingService;
import com.iteaj.izone.spi.event.EventPublisher;
import com.iteaj.izone.web.shiro.ShiroAuthToken;
import com.iteaj.izone.web.shiro.ShiroAuthorEvent;
import com.iteaj.util.Result;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * create time: 2019/12/6
 *  框架权限认证服务
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping
@ConditionalOnBean(AuthorizingService.class)
public class FrameworkAuthController extends BaseController {

    private EventPublisher eventPublisher;
    private final AuthorizingService authorizingService;

    public FrameworkAuthController(ObjectProvider<EventPublisher> provider
            , AuthorizingService authorizingService) {
        this.authorizingService = authorizingService;
        this.eventPublisher = provider.getIfAvailable();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 表单提交登录功能
     * @param token
     * @return
     */
    @ResponseBody
    @PostMapping("/login")
    public Result login(@RequestBody ShiroAuthToken token) {
        return this.authorizingService.login(token);
    }

    /**
     * 注销
     */
    @ResponseBody
    @RequestMapping("/logout")
    public Result logout() {
        return this.authorizingService.logout(ShiroUtils.getPrincipal());
    }

    /**
     * 返回环境相关配置
     * @return
     */
    @ResponseBody
    @GetMapping("/env")
    public Result envConfig() {
        Map<String, Object> config =  authorizingService
                .getEnvConfig(ShiroUtils.getPrincipal());
        return success().add("env", config);
    }

    /**
     * 权限资源列表
     * @return
     */
    @ResponseBody
    @GetMapping("/resources")
    public Result resources() {
        BaseEntity principal = ShiroUtils.getPrincipal();
        return success().add("resources", authorizingService.getResources(principal));
    }

    /**
     * 获取视图菜单下面的功能点
     * @param id 指定视图
     * @return
     */
    @ResponseBody
    @GetMapping("/point")
    public Result getFunctionPoint(Long id) {
        return success().add("rows", authorizingService.getFunctionPoint(id));
    }

}
