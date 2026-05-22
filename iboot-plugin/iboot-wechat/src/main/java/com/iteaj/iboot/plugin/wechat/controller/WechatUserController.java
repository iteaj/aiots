package com.iteaj.iboot.plugin.wechat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.plugin.wechat.entity.WechatUser;
import com.iteaj.iboot.plugin.wechat.service.IWechatUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信用户管理
 * @author iteaj
 * @since 2026-05-22
 */
@RestController
@RequestMapping("/wechat/user")
public class WechatUserController extends BaseController {

    private final IWechatUserService wechatUserService;

    public WechatUserController(IWechatUserService wechatUserService) {
        this.wechatUserService = wechatUserService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询微信用户列表")
    @GetMapping("/view")
    @CheckPermission("wechat:user:view")
    public Result<IPage<WechatUser>> view(Page page, WechatUser wechatUser) {
        return this.wechatUserService.page(page, wechatUser);
    }

    /**
     * 列表查询
     */
    @Logger("查询微信用户列表")
    @GetMapping("/list")
    @CheckPermission("wechat:user:view")
    public Result<List<WechatUser>> list(WechatUser wechatUser) {
        return wechatUserService.list(wechatUser);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取微信用户编辑记录")
    @GetMapping("/edit")
    public Result<WechatUser> edit(Long id) {
        return this.wechatUserService.getById(id);
    }

    /**
     * 获取记录详情
     */
    @Logger("获取微信用户详情")
    @GetMapping("/detail")
    public Result<WechatUser> detail(Long id) {
        return this.wechatUserService.getById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或更新微信用户")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"wechat:user:edit", "wechat:user:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody WechatUser wechatUser) {
        return this.wechatUserService.saveOrUpdate(wechatUser);
    }

    /**
     * 删除
     */
    @Logger("删除微信用户")
    @PostMapping("/del")
    @CheckPermission("wechat:user:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.wechatUserService.removeByIds(ids);
    }
}
