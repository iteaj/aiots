package com.iteaj.iboot.plugin.wechat.dto;

import com.iteaj.iboot.plugin.wechat.enums.UserCategory;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序授权登录 DTO
 * @author iteaj
 */
@Getter
@Setter
public class WechatAuthorizeDto {

    /** 临时登录凭证 */
    private String code;

    /** 加密算法的初始向量 */
    private String iv;

    /** 加密数据（包含用户信息） */
    private String encryptedData;

    /** 用户类别 */
    private UserCategory userCategory;
}
