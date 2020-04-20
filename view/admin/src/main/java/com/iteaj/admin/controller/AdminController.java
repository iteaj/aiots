package com.iteaj.admin.controller;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.admin.dto.AdminDto;
import com.iteaj.admin.entity.Admin;
import com.iteaj.admin.service.IAdminService;
import com.iteaj.framework.BaseController;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private IAdminService adminService;

    @GetMapping
    @RequiresPermissions("core:admin:view")
    public String view() {
        return "admin";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:admin:view")
    public Result view(Page page, Admin admin) {
        return this.adminService.selectByPage(page, admin).response(success());
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
        return this.adminService.getAdminDetailById(id).response(success());
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
