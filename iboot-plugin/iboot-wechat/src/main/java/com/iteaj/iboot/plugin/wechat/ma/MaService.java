package com.iteaj.iboot.plugin.wechat.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.iboot.plugin.wechat.entity.SocialUser;
import com.iteaj.iboot.plugin.wechat.enums.UserCategory;
import com.iteaj.iboot.plugin.wechat.service.ISocialUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 小程序登录服务
 * @author iteaj
 */
@Service
@ConditionalOnProperty(prefix = "wechat.ma", name = "app-id")
public class MaService {

    private final WxMaService wxMaService;
    private final ISocialUserService socialUserService;

    public MaService(WxMaService wxMaService, ISocialUserService socialUserService) {
        this.wxMaService = wxMaService;
        this.socialUserService = socialUserService;
    }

    /**
     * 小程序登录（code2session）
     * @param code 临时登录凭证
     * @param userCategory 用户类别
     * @return 微信用户
     */
    public SocialUser login(String code, UserCategory userCategory) throws WxErrorException {
        WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);

        // 查询或创建微信用户绑定
        SocialUser socialUser = socialUserService.getOne(
                Wrappers.<SocialUser>lambdaQuery()
                        .eq(SocialUser::getPlatform, "WECHAT_MA")
                        .eq(SocialUser::getOpenId, session.getOpenid())
        ).getData();

        if (socialUser != null) {
            socialUser.setSessionKey(session.getSessionKey())
                    .setUnionId(session.getUnionid());
            socialUserService.updateById(socialUser);
        } else {
            socialUser = new SocialUser();
            socialUser.setPlatform("WECHAT_MA")
                    .setOpenId(session.getOpenid())
                    .setUnionId(session.getUnionid())
                    .setSessionKey(session.getSessionKey());
            socialUserService.save(socialUser);
        }

        return socialUser;
    }

    /**
     * 小程序授权登录（完整流程：code2session + 解密用户信息 + 更新微信用户记录）
     * @param code 临时登录凭证
     * @param iv 加密算法的初始向量
     * @param encryptedData 加密数据（包含用户信息）
     * @param userCategory 用户类别
     * @return 微信用户（含解密后的昵称、头像等信息）
     */
    public SocialUser authorize(String code, String iv, String encryptedData, UserCategory userCategory) throws WxErrorException {
        // 1. code2session
        WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);

        // 2. 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(
                session.getSessionKey(), encryptedData, iv
        );

        // 3. 查询或创建微信用户绑定
        SocialUser socialUser = socialUserService.getOne(
                Wrappers.<SocialUser>lambdaQuery()
                        .eq(SocialUser::getPlatform, "WECHAT_MA")
                        .eq(SocialUser::getOpenId, session.getOpenid())
        ).getData();

        if (socialUser != null) {
            socialUser.setSessionKey(session.getSessionKey())
                    .setUnionId(session.getUnionid());
            if (userInfo != null) {
                socialUser.setNickname(userInfo.getNickName())
                        .setAvatar(userInfo.getAvatarUrl())
                        .setGender(userInfo.getGender() != null ? Integer.parseInt(userInfo.getGender()) : 0);
            }
            socialUserService.updateById(socialUser);
        } else {
            socialUser = new SocialUser();
            socialUser.setPlatform("WECHAT_MA")
                    .setOpenId(session.getOpenid())
                    .setUnionId(session.getUnionid())
                    .setSessionKey(session.getSessionKey());
            if (userInfo != null) {
                socialUser.setNickname(userInfo.getNickName())
                        .setAvatar(userInfo.getAvatarUrl())
                        .setGender(userInfo.getGender() != null ? Integer.parseInt(userInfo.getGender()) : 0);
            }
            socialUserService.save(socialUser);
        }

        return socialUser;
    }

    /**
     * 绑定微信用户到系统用户（管理端）
     * @param openId 微信openId
     * @param systemUserId 系统用户ID
     */
    public void bindSystemUser(String openId, Long systemUserId) {
        socialUserService.update(
                Wrappers.<SocialUser>lambdaUpdate()
                        .eq(SocialUser::getPlatform, "WECHAT_MA")
                        .eq(SocialUser::getOpenId, openId)
                        .set(SocialUser::getSystemUserId, systemUserId)
        );
    }

    /**
     * 获取手机号
     * @param openId 用户openId
     * @param code 手机号临时凭证
     * @return 手机号
     */
    public String getPhone(String openId, String code) throws WxErrorException {
        WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService().getPhoneNoInfo(code);

        // 更新微信用户手机号
        socialUserService.update(
                Wrappers.<SocialUser>lambdaUpdate()
                        .eq(SocialUser::getPlatform, "WECHAT_MA")
                        .eq(SocialUser::getOpenId, openId)
                        .set(SocialUser::getPhone, phoneInfo.getPhoneNumber())
        );

        return phoneInfo.getPhoneNumber();
    }
}
