package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.LinkageCondition;
import com.iteaj.iboot.module.iot.service.ILinkageConditionService;
import com.iteaj.framework.BaseController;

/**
 * 联动条件管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/linkageCondition")
public class LinkageConditionController extends BaseController {

    private final ILinkageConditionService linkageConditionService;

    public LinkageConditionController(ILinkageConditionService linkageConditionService) {
        this.linkageConditionService = linkageConditionService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览联动条件功能")
    @CheckPermission({"iot:linkageCondition:view"})
    public Result<IPage<LinkageCondition>> list(Page<LinkageCondition> page, LinkageCondition entity) {
        return this.linkageConditionService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:linkageCondition:edit"})
    public Result<LinkageCondition> getById(Long id) {
        return this.linkageConditionService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新联动条件记录")
    @CheckPermission(value = {"iot:linkageCondition:edit", "iot:linkageCondition:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody LinkageCondition entity) {
        return this.linkageConditionService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除联动条件记录")
    @CheckPermission({"iot:linkageCondition:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.linkageConditionService.removeByIds(idList);
    }
}

