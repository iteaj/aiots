package com.iteaj.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.core.dto.AdminDto;
import com.iteaj.core.entity.Admin;
import com.iteaj.core.service.IAdminService;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2019/11/27
 *
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/core/admin")
public class AdminController extends BaseController {

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @RequiresPermissions("core:admin:view")
    public String view() {
        return "admin";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:admin:view")
    public Result view(Page page, Admin admin) {
        return this.adminService.selectByPage(page, admin).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:admin:add")
    public Result add(@RequestBody AdminDto admin) {
        this.adminService.createAdmin(admin);
        return success();
    }

    @ResponseBody
    @GetMapping("/edit")
    public Result edit(Long id) {
        return this.adminService.getAdminDetailById(id).build(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:admin:edit")
    public Result edit(@RequestBody AdminDto admin) {
        this.adminService.updateAdminAndRole(admin);
        return success();
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:admin:del")
    public Result del(@RequestBody List<Long> list) {
        this.adminService.deleteAllJoinByIds(list);
        return success();
    }

    @ResponseBody
    @PostMapping("/modUserInfo")
    public Result modUserInfo(@RequestBody Admin admin) {
        BaseEntity principal = SecurityUtils.getPrincipal();
        if(principal == null) return fail("未登录");

        admin.setId(principal.getId());
        this.adminService.updateCurrentUserInfo(admin);
        return success("修改成功");
    }

    /**
     * 更新密码
     * @return
     */
    @ResponseBody
    @PostMapping("pwd")
    public Result updatePwd(@RequestBody AdminDto adminDto) {
        this.adminService.updatePwdById(adminDto.getId(), adminDto.getPassword());
        return success();
    }
}
