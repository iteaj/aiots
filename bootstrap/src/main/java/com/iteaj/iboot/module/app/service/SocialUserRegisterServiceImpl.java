package com.iteaj.iboot.module.app.service;

import cn.hutool.crypto.SecureUtil;
import com.iteaj.framework.consts.UseStatus;
import com.iteaj.iboot.module.core.entity.Admin;
import com.iteaj.iboot.module.core.service.IAdminService;
import com.iteaj.iboot.plugin.wechat.entity.SocialUser;
import com.iteaj.iboot.plugin.wechat.service.ISocialUserService;
import com.iteaj.iboot.plugin.wechat.spi.SocialUserRegisterService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 社交用户注册SPI实现：将社交用户关联到系统用户(Admin)
 * @author iteaj
 */
@Service
public class SocialUserRegisterServiceImpl implements SocialUserRegisterService {

    private final IAdminService adminService;
    private final ISocialUserService socialUserService;

    public SocialUserRegisterServiceImpl(IAdminService adminService,
                                         ISocialUserService socialUserService) {
        this.adminService = adminService;
        this.socialUserService = socialUserService;
    }

    @Override
    public Long registerOrGet(SocialUser socialUser) {
        // 1. 如果已关联系统用户，直接返回
        if (socialUser.getSystemUserId() != null) {
            Admin admin = adminService.getById(socialUser.getSystemUserId()).getData();
            if (admin != null) {
                return admin.getId();
            }
        }

        // 2. 创建新的系统用户
        Admin admin = new Admin();
        String openId = socialUser.getOpenId();
        admin.setAccount("wx_" + (openId.length() > 30 ? openId.substring(0, 30) : openId));
        admin.setName(socialUser.getNickname() != null ? socialUser.getNickname() : "微信用户");
        admin.setAvatar(socialUser.getAvatar());
        if (socialUser.getGender() != null) {
            admin.setSex(String.valueOf(socialUser.getGender()));
        }
        admin.setPassword(SecureUtil.md5(UUID.randomUUID().toString()));
        admin.setStatus(UseStatus.enabled);
        adminService.saveOrUpdate(admin);

        // 3. 关联社交用户到系统用户
        socialUser.setSystemUserId(admin.getId());
        socialUserService.updateById(socialUser);

        return admin.getId();
    }
}
