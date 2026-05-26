package com.iteaj.iboot.plugin.wechat.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序绑定管理端用户 DTO
 * @author iteaj
 */
@Getter
@Setter
public class WechatBindDto {

    /** 微信openId */
    private String openId;

    /** 管理端账号 */
    private String account;

    /** 管理端密码 */
    private String password;
}
