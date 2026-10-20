package com.iteaj.iboot.module.app.dto;

import lombok.Data;

/**
 * 绑定管理端账号 DTO
 * @author iteaj
 */
@Data
public class BindDto {

    /** 平台标识（如 WECHAT_MA、WECHAT_MP 等） */
    private String platform;

    /** 微信openId */
    private String openId;

    /** 管理端账号 */
    private String account;

    /** 管理端密码 */
    private String password;
}
