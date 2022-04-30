package com.iteaj.iboot.msn.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.iboot.msn.core.dto.AdminDto;
import com.iteaj.iboot.msn.core.dto.PasswordDto;
import com.iteaj.iboot.msn.core.entity.Admin;
import com.iteaj.iboot.msn.core.service.IAdminService;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  后台用户管理功能
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

    /**
     * 查询用户管理列表
     * @param page
     * @param admin
     * @return
     */
    @GetMapping("/view")
    @RequiresPermissions("core:admin:view")
    public Result view(Page page, Admin admin) {
        return this.adminService.page(page, admin);
    }

    /**
     * 新增用户
     * @param admin
     * @return
     */
    @PostMapping("/add")
    @RequiresPermissions("core:admin:add")
    public Result add(@RequestBody AdminDto admin) {
        this.adminService.createAdmin(admin);
        return success();
    }

    /**
     * 获取用户详情
     * @param id
     * @return
     */
    @GetMapping("/edit")
    @RequiresPermissions("core:admin:edit")
    public Result edit(Long id) {
        return this.adminService.getAdminDetailById(id);
    }

    /**
     * 修改用户
     * @param admin
     * @return
     */
    @PostMapping("/edit")
    @RequiresPermissions("core:admin:edit")
    public Result edit(@RequestBody AdminDto admin) {
        this.adminService.updateAdminAndRole(admin);
        return success();
    }

    /**
     * 删除用户
     * @param list
     * @return
     */
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
     * 设置用户密码
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
