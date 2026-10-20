package com.iteaj.iboot.module.app.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.app.entity.AppUser;
import com.iteaj.iboot.module.app.service.IAppUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动端用户管理模块
 */
@RestController
@RequestMapping("/app/user")
public class UserController extends BaseController {

    private final IAppUserService appUserService;

    public UserController(IAppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * 分页查询用户列表
     */
    @Logger("分页查询用户列表")
    @GetMapping("/view")
    public Result<IPage<AppUser>> view(Page page, AppUser appUser) {
        return this.appUserService.page(page, appUser);
    }

    /**
     * 列表查询
     */
    @Logger("查询用户列表")
    @GetMapping("/list")
    public Result<List<AppUser>> list(AppUser appUser) {
        return appUserService.list(appUser);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取用户编辑记录")
    @GetMapping("/edit")
    public Result<AppUser> edit(Long id) {
        return this.appUserService.getById(id);
    }

    /**
     * 获取记录详情
     */
    @Logger("获取用户详情")
    @GetMapping("/detail")
    public Result<AppUser> detail(Long id) {
        return this.appUserService.getById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或更新用户")
    @PostMapping("/saveOrUpdate")
    public Result<Boolean> saveOrUpdate(@RequestBody AppUser appUser) {
        return this.appUserService.saveOrUpdate(appUser);
    }

    /**
     * 删除
     */
    @Logger("删除用户")
    @PostMapping("/del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.appUserService.removeByIds(ids);
    }
}
