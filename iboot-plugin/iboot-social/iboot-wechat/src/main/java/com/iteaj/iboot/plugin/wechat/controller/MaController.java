package com.iteaj.iboot.plugin.wechat.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.security.SecurityToken;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.app.AppSecurityUtil;
import com.iteaj.iboot.plugin.social.entity.SocialUser;
import com.iteaj.iboot.plugin.wechat.dto.WechatAuthorizeDto;
import com.iteaj.iboot.plugin.wechat.dto.WechatBindDto;
import com.iteaj.iboot.plugin.wechat.enums.UserCategory;
import com.iteaj.iboot.plugin.wechat.ma.MaService;
import com.iteaj.iboot.plugin.wechat.vo.WechatAuthVo;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 小程序接口
 * @author iteaj
 */
@RestController
@RequestMapping("/wx/ma")
@ConditionalOnProperty(prefix = "wechat.ma", name = "app-id")
public class MaController extends BaseController {

    private final MaService maService;

    public MaController(MaService maService) {
        this.maService = maService;
    }

    /**
     * 小程序登录（code2session）
     * @param code 临时登录凭证
     * @param userCategory 用户类别
     */
    @PostMapping("/login")
    public Result<SocialUser> login(String code, UserCategory userCategory) {
        try {
            SocialUser socialUser = maService.login(code, userCategory);
            return success(socialUser, "登录成功");
        } catch (WxErrorException e) {
            return fail(null, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 小程序授权登录（获取用户信息 + 关联系统用户 + 生成Token）
     * @param dto 包含 code, iv, encryptedData, userCategory
     */
    @PostMapping("/authorize")
    public Result<WechatAuthVo> authorize(@RequestBody WechatAuthorizeDto dto) {
        String code = dto.getCode();
        String iv = dto.getIv();
        String encryptedData = dto.getEncryptedData();
        UserCategory userCategory = dto.getUserCategory();

        if (code == null || iv == null || encryptedData == null) {
            return fail("参数不完整");
        }

        try {
            // 1. 微信授权登录（code2session + 解密用户信息）
            SocialUser socialUser = maService.authorize(code, iv, encryptedData, userCategory);

            // 2. 会员端直接登录，无需绑定管理端用户
            if (userCategory == UserCategory.MEMBER) {
                AppSecurityUtil.login(socialUser);
                return success(buildAuthVo(), "登录成功");
            }

            // 3. 管理端：已关联系统用户则直接登录，否则需先绑定
            if (socialUser.getSystemUserId() != null) {
                AppSecurityUtil.login(socialUser);
                return success(buildAuthVo(), "登录成功");
            } else {
                WechatAuthVo vo = new WechatAuthVo()
                        .setOpenId(socialUser.getOpenId())
                        .setNickname(socialUser.getNickname())
                        .setAvatar(socialUser.getAvatar());
                return success(vo, "请先绑定管理端用户");
            }
        } catch (WxErrorException e) {
            return fail("微信登录失败：" + e.getMessage());
        }
    }

    /**
     * 构建Token信息
     */
    private WechatAuthVo buildAuthVo() {
        return new WechatAuthVo()
                .setToken(AppSecurityUtil.getTokenValue())
                .setExpire(AppSecurityUtil.getTokenTimeout())
                .setRefreshToken(AppSecurityUtil.getTokenValue())
                .setRefreshExpire(AppSecurityUtil.getTokenTimeout())
                .setTokenName(AppSecurityUtil.getTokenName());
    }

    /**
     * 管理端绑定：账号密码登录并绑定微信用户
     * @param dto 包含 openId, account, password
     */
    @PostMapping("/bind")
    public Result<WechatAuthVo> bind(@RequestBody WechatBindDto dto,
                                     HttpServletRequest request, HttpServletResponse response) {
        String openId = dto.getOpenId();
        String account = dto.getAccount();
        String password = dto.getPassword();

        if (openId == null || account == null || password == null) {
            return fail("参数不完整");
        }

        try {
            SecurityToken token = new SecurityToken();
            token.setAccount(account);
            token.setPassword(password);
            SecurityUtil.login(token, request, response);

            Long loginId = SecurityUtil.getLoginId()
                    .orElseThrow(() -> new RuntimeException("登录后获取用户ID失败"));
            SocialUser socialUser = maService.bindSystemUser(openId, loginId);
            AppSecurityUtil.login(socialUser);
            return success(buildAuthVo(), "绑定成功");
        } catch (SecurityException e) {
            return fail("账号或密码错误");
        } catch (Exception e) {
            return fail("绑定失败：" + e.getMessage());
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
