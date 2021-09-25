package com.iteaj.msn.core.controller;


import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import com.iteaj.framework.BaseController;

/**
 * <p>
 * 在线用户管理
 * </p>
 *
 * @author iteaj
 * @since 2021-09-25
 */
@RestController
@RequestMapping("/core/onlineUser")
public class OnlineUserController extends BaseController {

    private final IOnlineUserService onlineUserService;

    public OnlineUserController(IOnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity
    */
    @GetMapping("/view")
    @RequiresPermissions(value = {"core:onlineUser:view"})
    public Result<IPage<OnlineUser>> list(Page<OnlineUser> page, OnlineUser entity) {
        return this.onlineUserService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions(value = {"core:onlineUser:edit"})
    public Result<OnlineUser> getEditDetail(Serializable id) {
        return this.onlineUserService.getById(entity);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @RequiresPermissions(value = {"core:onlineUser:edit"})
    public Result<Boolean> edit(@RequestBody OnlineUser entity) {
        return this.onlineUserService.updateById(entity);
    }

    /**
    * 新增或者修改记录
    * @param entity
    */
    @PostMapping("/add")
    @RequiresPermissions(value = {"core:onlineUser:add"})
    public Result<Boolean> add(@RequestBody OnlineUser entity) {
        return this.onlineUserService.save(entity);
    }

    /**
    * 删除记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions(value = {"core:onlineUser:del"})
    public Result<Boolean> remove(@RequestBody List<Serializable> idList) {
        return this.onlineUserService.removeByIds(idList);
    }
}

