package com.iteaj.iboot.plugin.oauth2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.Entity;
import lombok.Data;

@Data
@TableName("oauth2_app")
public class Oauth2App implements Entity<Long> {

    private Long id;

    private String clientId;

    private String clientSecret;

    private String contractScope;

    private String allowUrl;

    private String clientName;

    private Long accessTokenTimeout;

    private Long refreshTokenTimeout;

    private Long clientTokenTimeout;

    private Long pastTokenTimeout;

    private String createTime;
}
