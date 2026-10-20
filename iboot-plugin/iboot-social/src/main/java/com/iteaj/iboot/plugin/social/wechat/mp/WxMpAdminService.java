package com.iteaj.iboot.plugin.social.wechat.mp;

import cn.hutool.crypto.SecureUtil;
import com.iteaj.iboot.plugin.social.entity.SocialAdmin;
import com.iteaj.iboot.plugin.social.mapper.SocialAdminMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 微信登录自动注册管理端用户服务
 * @author iteaj
 */
@Service
@ConditionalOnProperty(prefix = "wechat.mp", name = "app-id")
public class WxMpAdminService {

    private final SocialAdminMapper adminMapper;

    public WxMpAdminService(SocialAdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    /**
     * 为微信用户创建管理端账号
     * @param account 账号（使用微信openId）
     * @param name 昵称
     * @param avatar 头像
     * @return 新建的用户ID
     */
    public Long createWechatUser(String account, String name, String avatar) {
        SocialAdmin admin = new SocialAdmin();
        admin.setAccount(account);
        admin.setName(name != null ? name : account);
        admin.setAvatar(avatar);
        admin.setPassword(SecureUtil.md5(account));
        admin.setStatus("enabled");
        admin.setCreateTime(new Date());
        admin.setUpdateTime(new Date());
        adminMapper.insert(admin);
        return admin.getId();
    }
}
