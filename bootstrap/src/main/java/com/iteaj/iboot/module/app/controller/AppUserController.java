package com.iteaj.iboot.module.app.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.app.dto.UpdatePersonDto;
import com.iteaj.iboot.module.app.service.IAppUserService;
import com.iteaj.iboot.module.app.vo.PersonVo;
import com.iteaj.iboot.module.app.entity.AppUser;
import com.iteaj.framework.spi.app.AppSecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 移动端用户接口
 * @author iteaj
 */
@RestController
@RequestMapping("/m/user")
public class AppUserController extends BaseController {

    private final IAppUserService appUserService;

    public AppUserController(IAppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info/person")
    public Result<PersonVo> person() {
        Object loginId = AppSecurityUtil.getLoginId();
        if (loginId == null) {
            return fail("未登录");
        }

        AppUser appUser = appUserService.getById(loginId.toString()).getData();
        if (appUser == null) {
            return fail("用户不存在");
        }

        PersonVo vo = new PersonVo();
        vo.setId(appUser.getId());
        vo.setUnionid(appUser.getUnionId() != null ? appUser.getUnionId() : "");
        vo.setNickName(appUser.getNickname());
        vo.setAvatarUrl(appUser.getAvatar());
        vo.setPhone(appUser.getPhone());
        vo.setGender(appUser.getGender() != null ? appUser.getGender() : 0);
        vo.setStatus(1);
        vo.setDescription("");
        vo.setLoginType(2);
        vo.setProvince("");
        vo.setCity("");
        vo.setDistrict("");
        vo.setBirthday("");
        vo.setCreateTime(formatDate(appUser.getCreateTime()));
        vo.setUpdateTime(formatDate(appUser.getUpdateTime()));

        return success(vo, "获取成功");
    }

    /**
     * 更新当前登录用户信息
     */
    @PostMapping("/info/updatePerson")
    public Result<?> updatePerson(@RequestBody UpdatePersonDto dto) {
        Object loginId = AppSecurityUtil.getLoginId();
        if (loginId == null) {
            return fail("未认证");
        }

        AppUser appUser = appUserService.getById(loginId.toString()).getData();
        if (appUser == null) {
            return fail("用户不存在");
        }

        if (dto.getNickName() != null && !dto.getNickName().trim().isEmpty()) {
            appUser.setNickname(dto.getNickName());
        }
        if (dto.getAvatarUrl() != null && !dto.getAvatarUrl().trim().isEmpty()) {
            appUser.setAvatar(dto.getAvatarUrl());
        }
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            appUser.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            appUser.setGender(dto.getGender());
        }

        appUserService.updateById(appUser);
        return success("更新成功");
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
