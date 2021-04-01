package com.iteaj.framework.spring.boot.config;

import lombok.Data;

/**
 * create time: 2021/3/27
 *  认证相关配置信息
 * @author iteaj
 * @since 1.0
 */
@Data
public class AuthConfig {

    /**
     * 系统统一注销地址
     */
    private String logoutUri = "/auth/logout";


    /**
     * 原生app请求头的token名称
     */
    private String tokenName = "access_token";
}
