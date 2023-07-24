package com.iteaj.iboot.plugin.oauth2.sa;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2App;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2AppService;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2UserService;
import org.springframework.stereotype.Component;

@Component
public class SaOAuth2TemplateImpl extends SaOAuth2Template {

    private final Oauth2AppService oauth2AppService;
    private final Oauth2UserService oauth2UserService;

    public SaOAuth2TemplateImpl(Oauth2AppService oauth2AppService
            , Oauth2UserService oauth2UserService) {
        this.oauth2AppService = oauth2AppService;
        this.oauth2UserService = oauth2UserService;
    }

    // 根据 id 获取 Client 信息
    @Override
    public SaClientModel getClientModel(String clientId) {
        oauth2AppService.getByClientId(clientId).ofNullable().map(item -> {
            SaClientModel clientModel = new SaClientModel();

            return clientModel;
        }).orElse(null);

        // 此为模拟数据，真实环境需要从数据库查询
        if("1001".equals(clientId)) {
            return new SaClientModel()
                    .setClientId("1001")
                    .setClientSecret("aaaa-bbbb-cccc-dddd-eeee")
                    .setAllowUrl("*")
                    .setContractScope("userinfo")
                    .setIsAutoMode(true);
        }

        return null;
    }

    // 根据ClientId 和 LoginId 获取openid
    @Override
    public String getOpenid(String clientId, Object loginId) {
        // 此为模拟数据，真实环境需要从数据库查询
        return "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__";
    }
}
