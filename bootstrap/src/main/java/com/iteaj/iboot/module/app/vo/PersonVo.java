package com.iteaj.iboot.module.app.vo;

import lombok.Data;

/**
 * 移动端用户个人信息VO
 */
@Data
public class PersonVo {
    private Long id;
    /** 微信开放平台唯一ID */
    private String unionid;
    /** 用户昵称 */
    private String nickName;
    /** 头像地址 */
    private String avatarUrl;
    /** 手机号 */
    private String phone;
    /** 性别 0-未知 1-男 2-女 */
    private Integer gender;
    /** 状态 0-禁用 1-启用 */
    private Integer status;
    /** 个人描述 */
    private String description;
    /** 登录类型 1-账号 2-微信 */
    private Integer loginType;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区县 */
    private String district;
    /** 生日 */
    private String birthday;
    /** 创建时间 */
    private String createTime;
    /** 更新时间 */
    private String updateTime;
}
