package com.iteaj.iboot.plugin.wechat.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.wechat.entity.WechatUser;
import com.iteaj.iboot.plugin.wechat.ma.MaService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序接口
 * @author iteaj
 */
@RestController
@RequestMapping("/wechat/ma")
@ConditionalOnProperty(prefix = "wechat.ma", name = "app-id")
public class MaController extends BaseController {

    private final MaService maService;

    public MaController(MaService maService) {
        this.maService = maService;
    }

    /**
     * 小程序登录
     * @param code 临时登录凭证
     * @param userCategory 用户类别：1-管理端 2-会员端
     */
    @PostMapping("/login")
    public Result<WechatUser> login(String code, Integer userCategory) {
        try {
            WechatUser wechatUser = maService.login(code, userCategory);
            return success(wechatUser, "登录成功");
        } catch (WxErrorException e) {
            return fail(null, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取手机号
     * @param openId 用户openId
     * @param code 手机号临时凭证
     */
    @GetMapping("/phone")
    public Result<String> getPhone(String openId, String code) {
        try {
            String phone = maService.getPhone(openId, code);
            return success(phone, "获取成功");
        } catch (WxErrorException e) {
            return fail(null, "获取失败：" + e.getMessage());
        }
    }
}
