package com.iteaj.iboot.plugin.oauth2.sa;

import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2AppService;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2UserService;

public class SaOAuth2TemplateImpl {

    private final Oauth2AppService oauth2AppService;
    private final Oauth2UserService oauth2UserService;

    public SaOAuth2TemplateImpl(Oauth2AppService oauth2AppService
            , Oauth2UserService oauth2UserService) {
        this.oauth2AppService = oauth2AppService;
        this.oauth2UserService = oauth2UserService;
    }

    // 根据 id 获取 Client 信息
    public SaClientModel getClientModel(String clientId) {
        return null;
    }

    // 根据ClientId 和 LoginId 获取openid
    public String getOpenid(String clientId, Object loginId) {
        return null;
    }
}
