package com.iteaj.iboot.plugin.oauth2.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.consts.UseStatus;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.OAuth2ClientService;
import com.iteaj.framework.spi.oauth2.OAuth2ClientUser;
import com.iteaj.framework.spi.oauth2.OAuth2Service;
import com.iteaj.framework.spi.oauth2.Oauth2Result;
import com.iteaj.iboot.plugin.oauth2.config.Oauth2ClientProperties;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2Admin;
import com.iteaj.iboot.plugin.oauth2.entity.Oauth2ClientUser;
import com.iteaj.iboot.plugin.oauth2.mapper.Oauth2ClientMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class Oauth2ClientServiceImpl implements OAuth2ClientService {

    private final Oauth2ClientMapper oauth2ClientMapper;
    private final Oauth2ClientProperties clientProperties;

    @Override
    public OAuth2ClientUser getUser(String openid, AuthType type) {
        return oauth2ClientMapper.selectOne(Wrappers.<Oauth2ClientUser>lambdaQuery()
                .eq(OAuth2ClientUser::getOpenid, openid)
                .eq(OAuth2ClientUser::getType, type));
    }

    @Override
    @Transactional
    public OAuth2ClientUser getAndCreateOAuth2ClientUser(Oauth2Result user) {
        if (user == null || StrUtil.isBlank(user.getOpenid())) {
            throw new ServiceException("获取第三方用户信息失败");
        }

        String account;
        OAuth2ClientUser clientUser = this.getUser(user.getOpenid(), user.getType());
        if(clientUser == null) {
            account = uniqueAccount(user);
            clientUser = createOauth2User(account, user);
            Oauth2Admin oauth2Admin = createSysAdmin(account, user);
            this.saveOauth2UserAndAdmin((Oauth2ClientUser) clientUser, oauth2Admin);
        }

        return clientUser;
    }

    private void saveOauth2UserAndAdmin(Oauth2ClientUser clientUser, Oauth2Admin oauth2Admin) {
        oauth2ClientMapper.insert(clientUser);
        oauth2ClientMapper.insertSysAdmin(oauth2Admin);
    }

    private OAuth2ClientUser createOauth2User(String account, Oauth2Result user) {
        return new Oauth2ClientUser().setUserType("sys").setType(user.getType())
                .setOpenid(user.getOpenid()).setNickname(user.getNickname())
                .setAccount(account).setAvatarUrl(user.getAvatar());
    }


    /** 按第三方用户查绑定, 无则自动建号并写绑定, 返回本地账号ID */
    private Oauth2Admin createSysAdmin(String account, Oauth2Result user) {
        Oauth2Admin dto = new Oauth2Admin();
        dto.setAccount(account).setSex("3");
        dto.setOrgId(clientProperties.getOrgId()).setName(StrUtil.blankToDefault(user.getNickname(), user.getUsername()));
        dto.setAvatar(user.getAvatar()).setEmail(user.getEmail()).setRoles(List.of(clientProperties.getRoleId()));
        dto.setStatus(UseStatus.enabled).setPostId(clientProperties.getPostId()).setRemark(user.getRemark());

        // 第三方登录不走密码, 置随机密码(createAdmin 内部会 md5)
        dto.setPassword(UUID.randomUUID().toString());
        return dto;
    }

    /** 生成唯一账号: <type>_<username>, 冲突则追加序号 */
    private String uniqueAccount(Oauth2Result user) {
        return user.getType().name().substring(0, 1) + "_" + StrUtil.blankToDefault(user.getUsername(), user.getOpenid());
    }

}
