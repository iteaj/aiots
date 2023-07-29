package com.iteaj.iboot.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.iboot.msn.core.dto.AdminDto;
import com.iteaj.iboot.msn.core.dto.PasswordDto;
import com.iteaj.iboot.msn.core.entity.Admin;
import com.iteaj.iboot.msn.core.entity.Menu;
import com.iteaj.iboot.msn.core.service.IAdminService;
import com.iteaj.iboot.msn.core.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * 后台用户管理中心
 */
@RestController
@RequestMapping("/core/center")
public class AdminCenterController extends BaseController {

    @Autowired
    private IMenuService menuService;
    @Autowired
    private IAdminService adminService;

    /**
     * 菜单栏
     * @return
     */
    @GetMapping("menus")
    public Result<List<Menu>> menus() {
        Long loginId = SecurityUtil.getLoginId().map(item -> (Long) item).orElse(null);
        return menuService.selectMenuBarTrees(loginId, SecurityUtil.isSuper());
    }

    /**
     * 上传用户头像
     * @param file
     * @return
     */
    @Logger("上传用户头像")
    @PostMapping("avatar")
    public Result<String> avatar(MultipartFile file) {
        return success("");
    }

    /**
     * 获取用户详情
     * @return
     */
    @GetMapping("detail")
    public Result<AdminDto> detail() {
        Serializable id = SecurityUtil
                .getLoginId()
                .orElse(null);

        return adminService.getAdminCenter(id);
    }

    /**
     * 修改用户
     * @param admin
     * @return
     */
    @Logger("需改用户中心用户")
    @PostMapping("editUser")
    public Result<Boolean> updateUser(@RequestBody Admin admin) {
        return adminService.updateById(admin);
    }

    /**
     * 修改用户密码
     * @return
     */
    @Logger("修改用户中心用户密码")
    @PostMapping("pwd")
    public Result<Boolean> updatePwd(@RequestBody PasswordDto passwordDto) {
        this.adminService.updatePwdById(passwordDto.getId()
                , passwordDto.getPassword(), passwordDto.getOldPwd());
        return success("修改成功");
    }
}
