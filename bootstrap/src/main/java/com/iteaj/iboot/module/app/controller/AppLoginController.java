package com.iteaj.iboot.module.app.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.app.AppSecurityUtil;
import com.iteaj.iboot.module.app.dto.BindDto;
import com.iteaj.iboot.module.app.dto.LoginDto;
import com.iteaj.iboot.module.app.entity.AppUser;
import com.iteaj.iboot.module.app.service.IAppUserService;
import com.iteaj.iboot.module.app.vo.LoginVo;
import com.iteaj.iboot.module.core.entity.Admin;
import com.iteaj.iboot.module.core.service.IAdminService;
import org.springframework.web.bind.annotation.*;

/**
 * 移动端登录接口
 * @author iteaj
 */
@RestController
@RequestMapping("/m")
public class AppLoginController extends BaseController {

    private final IAppUserService appUserService;
    private final IAdminService adminService;

    public AppLoginController(IAppUserService appUserService, IAdminService adminService) {
        this.appUserService = appUserService;
        this.adminService = adminService;
    }

    /**
     * 账号密码登录
     * @param dto 包含 account, password
     */
    @PostMapping("login")
    public Result<LoginVo> login(@RequestBody LoginDto dto) {
        String account = dto.getAccount();
        String password = dto.getPassword();

        if (account == null || password == null) {
            return fail("账号或密码不能为空");
        }

        try {
            Admin user = adminService.getByAccount(account);
            if (user == null) {
                return fail("账号不存在");
            }

            if (!user.allowLogin()) {
                return fail("账号已被禁用");
            }

            SecurityUtil.checkPassword(user, password);

            Long adminId = user.getId();

            // 检查是否存在AppUser记录，不存在则创建并绑定管理员
            AppUser appUser = appUserService.getOne(
                    Wrappers.<AppUser>lambdaQuery()
                            .eq(AppUser::getSystemUserId, adminId)
            ).getData();
            if (appUser == null) {
                appUser = new AppUser();
                appUser.setPlatform(AppUser.PLATFORM_NATIVE)
                        .setSystemUserId(adminId)
                        .setNickname(user.getName())
                        .setAvatar(user.getAvatar())
                        .setPhone(user.getPhone())
                        .setGender(user.getSex() != null ? Integer.parseInt(user.getSex()) : null)
                        .setCreateTime(user.getCreateTime())
                        .setUpdateTime(user.getUpdateTime());
                appUserService.save(appUser);
            }

            // App端登录（使用AppUser的ID，自动加APP:前缀）
            AppSecurityUtil.login(appUser);

            LoginVo vo = new LoginVo();
            vo.setToken(AppSecurityUtil.getTokenValue());
            vo.setExpire(AppSecurityUtil.getTokenTimeout());
            vo.setRefreshToken(AppSecurityUtil.getTokenValue());
            vo.setRefreshExpire(AppSecurityUtil.getTokenTimeout());
            vo.setTokenName(AppSecurityUtil.getTokenName());

            return success(vo, "登录成功");
        } catch (SecurityException e) {
            return fail(e.getMessage());
        } catch (Exception e) {
            return fail("登录失败：" + e.getMessage());
        }
    }

    /**
     * 三方授权平台绑定管理端账号
     * @param dto 包含 openId, account, password
     */
    @PostMapping("login/bind")
    public Result<LoginVo> bindAccount(@RequestBody BindDto dto) {
        String openId = dto.getOpenId();
        String account = dto.getAccount();
        String password = dto.getPassword();

        if (openId == null || account == null || password == null) {
            return fail("参数不完整");
        }

        try {
            // 直接验证账号密码
            Admin user = adminService.getByAccount(account);
            if (user == null) {
                return fail("账号不存在");
            }

            if (!user.allowLogin()) {
                return fail("账号已被禁用");
            }

            SecurityUtil.checkPassword(user, password);

            // 通过平台+openId查找微信用户
            AppUser appUser = appUserService.getOne(
                    Wrappers.<AppUser>lambdaQuery()
                            .eq(AppUser::getPlatform, dto.getPlatform())
                            .eq(AppUser::getOpenId, openId)
            ).ifNotPresentThrow("三方授权用户未找到").getData();

            // 检查此管理员是否已被其他账号绑定
            appUserService.getOne(
                    Wrappers.<AppUser>lambdaQuery()
                            .eq(AppUser::getSystemUserId, user.getId())
                            .eq(AppUser::getPlatform, dto.getPlatform())
                            .ne(AppUser::getId, appUser.getId())
            ).ifPresentThrow("此管理员已被其他账号绑定");

            appUser.setSystemUserId(user.getId());
            appUserService.updateById(appUser);
            AppSecurityUtil.login(appUser);

            LoginVo vo = new LoginVo();
            vo.setToken(AppSecurityUtil.getTokenValue());
            vo.setExpire(AppSecurityUtil.getTokenTimeout());
            vo.setRefreshToken(AppSecurityUtil.getTokenValue());
            vo.setRefreshExpire(AppSecurityUtil.getTokenTimeout());
            vo.setTokenName(AppSecurityUtil.getTokenName());
            return success(vo, "绑定成功");
        } catch (Exception e) {
            return fail("绑定失败：" + e.getMessage());
        }
    }
}
