package com.iteaj.iboot.plugin.wechat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 第三方社交用户绑定
 * @author iteaj
 * @since 2026-05-22
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("social_user")
public class SocialUser extends BaseEntity {

    /** 平台标识：WECHAT_MA-微信小程序 WECHAT_MP-微信公众号 ALIPAY-支付宝 */
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
