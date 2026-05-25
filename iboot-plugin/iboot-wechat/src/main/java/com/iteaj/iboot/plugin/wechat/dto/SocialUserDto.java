package com.iteaj.iboot.plugin.wechat.dto;

import com.iteaj.iboot.plugin.wechat.entity.SocialUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 微信用户 DTO数据模型
 * @author iteaj
 * @since 2026-05-22
 */
@Getter
@Setter
@Accessors(chain = true)
public class SocialUserDto extends SocialUser {

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;
}
