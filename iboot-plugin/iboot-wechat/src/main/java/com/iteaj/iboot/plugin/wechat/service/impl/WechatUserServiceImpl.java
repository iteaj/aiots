package com.iteaj.iboot.plugin.wechat.service.impl;

import com.iteaj.iboot.plugin.wechat.entity.WechatUser;
import com.iteaj.iboot.plugin.wechat.mapper.WechatUserMapper;
import com.iteaj.iboot.plugin.wechat.service.IWechatUserService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 微信用户 服务实现类
 * @author iteaj
 * @since 2026-05-22
 */
@Service
public class WechatUserServiceImpl extends BaseServiceImpl<WechatUserMapper, WechatUser> implements IWechatUserService {

}
