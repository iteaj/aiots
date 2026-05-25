package com.iteaj.iboot.plugin.wechat.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 微信授权登录 VO
 * @author iteaj
 */
@Getter
@Setter
@Accessors(chain = true)
public class WechatAuthVo {

    /** 微信openId */
    private String openId;

    /** 微信昵称 */
    private String nickname;

    /** 微信头像 */
    private String avatar;

    /** 登录凭证（null 表示未绑定，需先绑定） */
    private String token;

    /** 过期时间（秒） */
    private Long expire;

    /** 刷新凭证 */
    private String refreshToken;

    /** 刷新凭证过期时间（秒） */
    private Long refreshExpire;

    /** Token 名称 */
    private String tokenName;
}
