package com.iteaj.iboot.module.app.dto;

import lombok.Data;

/**
 * 移动端用户信息更新DTO
 */
@Data
public class UpdatePersonDto {
    /** 昵称 */
    private String nickName;
    /** 头像地址 */
    private String avatarUrl;
    /** 手机号 */
    private String phone;
    /** 性别 0-未知 1-男 2-女 */
    private Integer gender;
    /** 个人描述 */
    private String description;
}
