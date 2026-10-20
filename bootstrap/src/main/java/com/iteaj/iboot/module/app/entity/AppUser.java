package com.iteaj.iboot.module.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * App用户
 * @author iteaj
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("m_user")
public class AppUser extends BaseEntity {

    /** 原生 */
    public static final String PLATFORM_NATIVE = "NATIVE";
    /** 微信小程序 */
    public static final String PLATFORM_WECHAT_MA = "WECHAT_MA";
    /** 微信公众号 */
    public static final String PLATFORM_WECHAT_MP = "WECHAT_MP";
    /** 支付宝 */
    public static final String PLATFORM_ALIPAY = "ALIPAY";

    /** 平台标识 */
    private String platform;

    /** 平台用户唯一标识（微信openId等） */
    private String openId;

    /** 平台统一标识 */
    private String unionId;

    /** 小程序sessionKey */
    private String sessionKey;

    /** 关联系统用户ID */
    private Long systemUserId;

    /** 微信昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 性别：0-未知 1-男 2-女 */
    private Integer gender;

    /** 是否关注公众号：0-否 1-是 */
    private Integer subscribe;

    /** 关注时间 */
    private Date subscribeTime;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;
}
