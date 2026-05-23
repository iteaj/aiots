package com.iteaj.iboot.plugin.wechat.mp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.iboot.plugin.wechat.entity.WechatUser;
import com.iteaj.iboot.plugin.wechat.service.IWechatUserService;
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
    private final IWechatUserService wechatUserService;

    public MpService(WxMpService wxMpService, IWechatUserService wechatUserService) {
        this.wxMpService = wxMpService;
        this.wechatUserService = wechatUserService;
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
    public WechatUser handleCallback(String code) throws WxErrorException {
        WxOAuth2AccessToken token = wxMpService.getOAuth2Service().getAccessToken(code);
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(token, null);

        // 查询或创建微信用户绑定
        WechatUser wechatUser = wechatUserService.getOne(
                Wrappers.<WechatUser>lambdaQuery()
                        .eq(WechatUser::getUserType, 1)
                        .eq(WechatUser::getOpenId, userInfo.getOpenid())
        ).getData();

        if (wechatUser != null) {
            wechatUser.setNickname(userInfo.getNickname())
                    .setAvatar(userInfo.getHeadImgUrl())
                    .setGender(userInfo.getSex());
            wechatUserService.updateById(wechatUser);
        } else {
            wechatUser = new WechatUser();
            wechatUser.setUserType(1)
                    .setOpenId(userInfo.getOpenid())
                    .setUnionId(userInfo.getUnionId())
                    .setNickname(userInfo.getNickname())
                    .setAvatar(userInfo.getHeadImgUrl())
                    .setGender(userInfo.getSex());
            wechatUserService.save(wechatUser);
        }

        return wechatUser;
    }

    /**
     * 绑定微信用户到系统用户
     * @param openId 微信openId
     * @param systemUserId 系统用户ID
     */
    public void bindSystemUser(String openId, Long systemUserId) {
        wechatUserService.update(
                Wrappers.<WechatUser>lambdaUpdate()
                        .eq(WechatUser::getUserType, 1)
                        .eq(WechatUser::getOpenId, openId)
                        .set(WechatUser::getSystemUserId, systemUserId)
                        .set(WechatUser::getUserCategory, 1)
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
