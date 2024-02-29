package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.LinkageConditionItem;
import com.iteaj.iboot.module.iot.service.ILinkageConditionItemService;
import com.iteaj.framework.BaseController;

/**
 * 场景条件项管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/linkageConditionItem")
public class LinkageConditionItemController extends BaseController {

    private final ILinkageConditionItemService linkageConditionItemService;

    public LinkageConditionItemController(ILinkageConditionItemService linkageConditionItemService) {
        this.linkageConditionItemService = linkageConditionItemService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览场景条件项功能")
    @CheckPermission({"iot:linkageConditionItem:view"})
    public Result<IPage<LinkageConditionItem>> list(Page<LinkageConditionItem> page, LinkageConditionItem entity) {
        return this.linkageConditionItemService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:linkageConditionItem:edit"})
    public Result<LinkageConditionItem> getById(Long id) {
        return this.linkageConditionItemService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新场景条件项记录")
    @CheckPermission(value = {"iot:linkageConditionItem:edit", "iot:linkageConditionItem:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody LinkageConditionItem entity) {
        return this.linkageConditionItemService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除场景条件项记录")
    @CheckPermission({"iot:linkageConditionItem:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.linkageConditionItemService.removeByIds(idList);
    }
}

