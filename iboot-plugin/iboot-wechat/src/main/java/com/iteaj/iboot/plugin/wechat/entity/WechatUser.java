package com.iteaj.iboot.plugin.wechat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 微信用户绑定
 * @author iteaj
 * @since 2026-05-22
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wechat_user")
public class WechatUser extends BaseEntity {

    /** 应用类型：1-公众号 2-小程序 */
    private Integer userType;

    /** 微信openId */
    private String openId;

    /** 微信unionId */
    private String unionId;

    /** 小程序sessionKey */
    private String sessionKey;

    /** 关联系统用户ID */
    private Long systemUserId;

    /** 用户类别：1-管理端 2-会员端 */
    private Integer userCategory;

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
