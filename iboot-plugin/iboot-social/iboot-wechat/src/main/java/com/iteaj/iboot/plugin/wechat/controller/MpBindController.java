package com.iteaj.iboot.plugin.wechat.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.security.SecurityToken;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.iboot.plugin.wechat.mp.MpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 公众号绑定API（移动端调用）
 * @author iteaj
 */
@RestController
@RequestMapping("/wx/mp/bind")
@ConditionalOnProperty(prefix = "wechat.mp", name = "app-id")
public class MpBindController extends BaseController {

    private final MpService mpService;

    public MpBindController(MpService mpService) {
        this.mpService = mpService;
    }

    /**
     * 账号密码登录并绑定微信
     * @param body 包含 openId, account, password
     */
    @PostMapping
    public Result<Void> bind(@RequestBody Map<String, String> body,
                             HttpServletRequest request, HttpServletResponse response) {
        String openId = body.get("openId");
        String account = body.get("account");
        String password = body.get("password");

        if (openId == null || account == null || password == null) {
            return fail("参数不完整");
        }

        try {
            SecurityToken token = new SecurityToken();
            token.setAccount(account);
            token.setPassword(password);
            SecurityUtil.login(token, request, response);

            Long userId = SecurityUtil.getLoginId()
                    .orElseThrow(() -> new RuntimeException("登录后获取用户ID失败"));
            mpService.bindSystemUser(openId, userId);

            return success("绑定成功");
        } catch (SecurityException e) {
            return fail("账号或密码错误");
        } catch (Exception e) {
            return fail("绑定失败：" + e.getMessage());
        }
    }
}
