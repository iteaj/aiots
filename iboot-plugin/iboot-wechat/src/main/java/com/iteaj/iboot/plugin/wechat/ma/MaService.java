package com.iteaj.iboot.plugin.wechat.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.iboot.plugin.wechat.entity.WechatUser;
import com.iteaj.iboot.plugin.wechat.service.IWechatUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 小程序登录服务
 * @author iteaj
 */
@Service
@ConditionalOnProperty(prefix = "wechat.ma", name = "app-id")
public class MaService {

    private final WxMaService wxMaService;
    private final IWechatUserService wechatUserService;

    public MaService(WxMaService wxMaService, IWechatUserService wechatUserService) {
        this.wxMaService = wxMaService;
        this.wechatUserService = wechatUserService;
    }

    /**
     * 小程序登录（code2session）
     * @param code 临时登录凭证
     * @param userCategory 用户类别：1-管理端 2-会员端
     * @return 微信用户
     */
    public WechatUser login(String code, Integer userCategory) throws WxErrorException {
        WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);

        // 查询或创建微信用户绑定
        WechatUser wechatUser = wechatUserService.getOne(
                Wrappers.<WechatUser>lambdaQuery()
                        .eq(WechatUser::getUserType, 2)
                        .eq(WechatUser::getOpenId, session.getOpenid())
        ).getData();

        if (wechatUser != null) {
            wechatUser.setSessionKey(session.getSessionKey())
                    .setUnionId(session.getUnionid());
            if (userCategory != null) {
                wechatUser.setUserCategory(userCategory);
            }
            wechatUserService.updateById(wechatUser);
        } else {
            wechatUser = new WechatUser();
            wechatUser.setUserType(2)
                    .setOpenId(session.getOpenid())
                    .setUnionId(session.getUnionid())
                    .setSessionKey(session.getSessionKey())
                    .setUserCategory(userCategory);
            wechatUserService.save(wechatUser);
        }

        return wechatUser;
    }

    /**
     * 获取手机号
     * @param openId 用户openId
     * @param code 手机号临时凭证
     * @return 手机号
     */
    public String getPhone(String openId, String code) throws WxErrorException {
        WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService().getPhoneNoInfo(code);

        // 更新微信用户手机号
        wechatUserService.update(
                Wrappers.<WechatUser>lambdaUpdate()
                        .eq(WechatUser::getUserType, 2)
                        .eq(WechatUser::getOpenId, openId)
                        .set(WechatUser::getPhone, phoneInfo.getPhoneNumber())
        );

        return phoneInfo.getPhoneNumber();
    }
}
