package com.iteaj.iboot.plugin.oauth2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.Entity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("oauth2_user")
public class Oauth2User implements Entity<Long> {

    private Long id;

    private String appid;

    private String openid;

    private String loginId;

    private Date createTime;
}
