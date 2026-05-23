package com.iteaj.iboot.module.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.consts.UseStatus;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.app.dto.UpdatePersonDto;
import com.iteaj.iboot.module.app.vo.PersonVo;
import com.iteaj.iboot.module.core.entity.Admin;
import com.iteaj.iboot.module.core.service.IAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 移动端用户信息接口
 * @author iteaj
 */
@RestController
@RequestMapping("/m/user")
public class AppUserController extends BaseController {

    private final IAdminService adminService;

    public AppUserController(IAdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info/person")
    public Result<PersonVo> person() {
        Object loginId = StpUtil.getLoginId();
        if (loginId == null) {
            return fail("未登录");
        }

        Admin admin = adminService.getById(Long.parseLong(loginId.toString())).getData();
        if (admin == null) {
            return fail("用户不存在");
        }

        PersonVo vo = new PersonVo();
        vo.setId(admin.getId());
        vo.setUnionid("");
        vo.setNickName(admin.getName());
        vo.setAvatarUrl(admin.getAvatar());
        vo.setPhone(admin.getPhone());
        vo.setGender(mapSex(admin.getSex()));
        vo.setStatus(mapStatus(admin.getStatus()));
        vo.setDescription(admin.getRemark() != null ? admin.getRemark() : "");
        vo.setLoginType(1);
        vo.setProvince("");
        vo.setCity("");
        vo.setDistrict("");
        vo.setBirthday("");
        vo.setCreateTime(formatDate(admin.getCreateTime()));
        vo.setUpdateTime(formatDate(admin.getUpdateTime()));

        return success(vo, "获取成功");
    }

    /**
     * 更新当前登录用户信息
     */
    @PostMapping("/info/updatePerson")
    public Result<?> updatePerson(@RequestBody UpdatePersonDto dto) {
        Object loginId = StpUtil.getLoginId();
        if (loginId == null) {
            return fail("未认证");
        }

        Admin admin = adminService.getById(Long.parseLong(loginId.toString())).getData();
        if (admin == null) {
            return fail("用户不存在");
        }

        if (dto.getNickName() != null && !dto.getNickName().trim().isEmpty()) {
            admin.setName(dto.getNickName());
        }
        if (dto.getAvatarUrl() != null && !dto.getAvatarUrl().trim().isEmpty()) {
            admin.setAvatar(dto.getAvatarUrl());
        }
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            admin.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            System.out.println("性别已更新: " + dto.getGender());
            admin.setSex(String.valueOf(dto.getGender()));
        }
        if (dto.getDescription() != null && !dto.getDescription().trim().isEmpty()) {
            admin.setRemark(dto.getDescription());
        }

        adminService.updateById(admin);
        return success("更新成功");
    }

    private int mapSex(String sex) {
        if (sex == null) return 0;
        try {
            return Integer.parseInt(sex);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int mapStatus(UseStatus status) {
        if (status == null) return 0;
        return status == UseStatus.enabled ? 1 : 0;
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
