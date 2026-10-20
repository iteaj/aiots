package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.auth.AuthType;

/**
 * 第三方 OAuth2 授权服务(本系统作为客户端接入 gitee/github 等第三方平台授权登录)
 * <p>框架层只定义契约, 由具体插件(如 iboot-oauth2 基于 JustAuth)实现并注册为 bean;
 * 业务侧(core)注入全部实现, 按 {@link #support(AuthType)} 匹配对应平台, 与授权库解耦。</p>
 *
 * @author iteaj
 */
public interface OAuth2Service {

    /**
     * 是否支持指定授权平台
     * @param type 授权类型[gitee, github, ...]
     */
    boolean support(AuthType type);

    /**
     * 生成第三方授权页跳转URL
     * @param type  授权类型
     * @param state 防CSRF随机串, 授权回调时第三方原样带回, 由业务侧校验
     * @return 第三方授权页完整URL
     */
    String authorize(AuthType type, String state);

    /**
     * 授权回调: 用 code 换取第三方用户信息
     * @param type  授权类型
     * @param code  第三方回调返回的授权码
     * @param state 授权时传入、回调带回的随机串
     * @return 第三方用户信息
     */
    Oauth2Result getUserInfo(AuthType type, String code, String state);
}
