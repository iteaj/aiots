package com.iteaj.iboot.plugin.wechat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.plugin.wechat.entity.SocialUser;
import com.iteaj.iboot.plugin.wechat.service.ISocialUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交用户管理
 * @author iteaj
 * @since 2026-05-22
 */
@RestController
@RequestMapping("/social/user")
public class SocialUserController extends BaseController {

    private final ISocialUserService socialUserService;

    public SocialUserController(ISocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询社交用户列表")
    @GetMapping("/view")
    @CheckPermission("wechat:user:view")
    public Result<IPage<SocialUser>> view(Page page, SocialUser socialUser) {
        return this.socialUserService.page(page, socialUser);
    }

    /**
     * 列表查询
     */
    @Logger("查询社交用户列表")
    @GetMapping("/list")
    @CheckPermission("wechat:user:view")
    public Result<List<SocialUser>> list(SocialUser socialUser) {
        return socialUserService.list(socialUser);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取社交用户编辑记录")
    @GetMapping("/edit")
    public Result<SocialUser> edit(Long id) {
        return this.socialUserService.getById(id);
    }

    /**
     * 获取记录详情
     */
    @Logger("获取社交用户详情")
    @GetMapping("/detail")
    public Result<SocialUser> detail(Long id) {
        return this.socialUserService.getById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或更新社交用户")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"wechat:user:edit", "wechat:user:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody SocialUser socialUser) {
        return this.socialUserService.saveOrUpdate(socialUser);
    }

    /**
     * 删除
     */
    @Logger("删除社交用户")
    @PostMapping("/del")
    @CheckPermission("wechat:user:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.socialUserService.removeByIds(ids);
    }
}
