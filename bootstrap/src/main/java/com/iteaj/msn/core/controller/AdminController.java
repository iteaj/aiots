package com.iteaj.msn.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.utils.ShiroUtil;
import com.iteaj.msn.core.dto.AdminDto;
import com.iteaj.msn.core.dto.PasswordDto;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.service.IAdminService;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/admin")
public class AdminController extends BaseController {

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/view")
    @RequiresPermissions("core:admin:view")
    public Result view(Page page, Admin admin) {
        return this.adminService.page(page, admin).build(success());
    }

    @PostMapping("/add")
    @RequiresPermissions("core:admin:add")
    public Result add(@RequestBody AdminDto admin) {
        this.adminService.createAdmin(admin);
        return success();
    }

    @GetMapping("/edit")
    @RequiresPermissions("core:admin:edit")
    public Result edit(Long id) {
        return this.adminService.getAdminDetailById(id).build(success());
    }

    @PostMapping("/edit")
    @RequiresPermissions("core:admin:edit")
    public Result edit(@RequestBody AdminDto admin) {
        this.adminService.updateAdminAndRole(admin);
        return success();
    }

    @PostMapping("/del")
    @RequiresPermissions("core:admin:del")
    public Result del(@RequestBody List<Long> list) {
        this.adminService.deleteAllJoinByIds(list);
        return success();
    }

    @PostMapping("/modUserInfo")
    public Result modUserInfo(@RequestBody Admin admin) {
        BaseEntity principal = ShiroUtil.getUser();
        if(principal == null) return fail("未登录");

        admin.setId(principal.getId());
        this.adminService.updateCurrentUserInfo(admin);
        return success("修改成功");
    }

    /**
     * 更新密码
     * @return
     */
    @PostMapping("pwd")
    @RequiresPermissions("core:admin:pwd")
    public Result updatePwd(@RequestBody PasswordDto passwordDto) {
        this.adminService.setAdminPassword(passwordDto
                .getId(), passwordDto.getPassword());
        return success("设置成功");
    }
}
