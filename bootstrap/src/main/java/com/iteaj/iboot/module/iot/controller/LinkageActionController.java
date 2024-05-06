package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.LinkageAction;
import com.iteaj.iboot.module.iot.service.ILinkageActionService;
import com.iteaj.framework.BaseController;

/**
 * 联动动作管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/linkageAction")
public class LinkageActionController extends BaseController {

    private final ILinkageActionService linkageActionService;

    public LinkageActionController(ILinkageActionService linkageActionService) {
        this.linkageActionService = linkageActionService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览联动动作功能")
    @CheckPermission({"iot:linkageAction:view"})
    public Result<IPage<LinkageAction>> list(Page<LinkageAction> page, LinkageAction entity) {
        return this.linkageActionService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:linkageAction:edit"})
    public Result<LinkageAction> getById(Long id) {
        return this.linkageActionService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新联动动作记录")
    @CheckPermission(value = {"iot:linkageAction:edit", "iot:linkageAction:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody LinkageAction entity) {
        return this.linkageActionService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除联动动作记录")
    @CheckPermission({"iot:linkageAction:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.linkageActionService.removeByIds(idList);
    }
}

