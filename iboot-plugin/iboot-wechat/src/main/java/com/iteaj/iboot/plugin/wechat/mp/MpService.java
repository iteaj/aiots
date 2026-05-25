package com.iteaj.iboot.plugin.wechat.mp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.iboot.plugin.wechat.entity.SocialUser;
import com.iteaj.iboot.plugin.wechat.service.ISocialUserService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 公众号授权服务
 * @author iteaj
 */
@Service
@ConditionalOnProperty(prefix = "wechat.mp", name = "app-id")
public class MpService {

    private final WxMpService wxMpService;
    private final ISocialUserService socialUserService;

    public MpService(WxMpService wxMpService, ISocialUserService socialUserService) {
        this.wxMpService = wxMpService;
        this.socialUserService = socialUserService;
    }

    /**
     * 构建公众号授权URL
     * @param redirectUri 回调地址
     * @param state state参数
     * @return 授权URL
     */
    public String buildAuthUrl(String redirectUri, String state) {
        return wxMpService.getOAuth2Service().buildAuthorizationUrl(
                redirectUri,
                WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                state
        );
    }

    /**
     * 处理授权回调
     * @param code 授权码
     * @return 微信用户
     */
    public SocialUser handleCallback(String code) throws WxErrorException {
        WxOAuth2AccessToken token = wxMpService.getOAuth2Service().getAccessToken(code);
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(token, null);

        // 查询或创建微信用户绑定
        SocialUser socialUser = socialUserService.getOne(
                Wrappers.<SocialUser>lambdaQuery()
                        .eq(SocialUser::getPlatform, "WECHAT_MP")
                        .eq(SocialUser::getOpenId, userInfo.getOpenid())
        ).getData();

        if (socialUser != null) {
            socialUser.setNickname(userInfo.getNickname())
                    .setAvatar(userInfo.getHeadImgUrl())
                    .setGender(userInfo.getSex());
            socialUserService.updateById(socialUser);
        } else {
            socialUser = new SocialUser();
            socialUser.setPlatform("WECHAT_MP")
                    .setOpenId(userInfo.getOpenid())
                    .setUnionId(userInfo.getUnionId())
                    .setNickname(userInfo.getNickname())
                    .setAvatar(userInfo.getHeadImgUrl())
                    .setGender(userInfo.getSex());
            socialUserService.save(socialUser);
        }

        return socialUser;
    }

    /**
     * 绑定微信用户到系统用户
     * @param openId 微信openId
     * @param systemUserId 系统用户ID
     */
    public void bindSystemUser(String openId, Long systemUserId) {
        socialUserService.update(
                Wrappers.<SocialUser>lambdaUpdate()
                        .eq(SocialUser::getPlatform, "WECHAT_MP")
                        .eq(SocialUser::getOpenId, openId)
                        .set(SocialUser::getSystemUserId, systemUserId)
        );
    }

    /**
     * 处理微信服务器消息推送
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param signature 签名
     * @param echostr 随机字符串
     * @return echostr(验证通过)
     */
    public String handleVerify(String timestamp, String nonce, String signature, String echostr) {
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "invalid";
    }
}
