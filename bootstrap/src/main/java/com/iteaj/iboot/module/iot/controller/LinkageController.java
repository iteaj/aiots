package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.Linkage;
import com.iteaj.iboot.module.iot.service.ILinkageService;
import com.iteaj.framework.BaseController;

/**
 * 场景联动管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/linkage")
public class LinkageController extends BaseController {

    private final ILinkageService linkageService;

    public LinkageController(ILinkageService linkageService) {
        this.linkageService = linkageService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览场景联动功能")
    @CheckPermission({"iot:linkage:view"})
    public Result<IPage<Linkage>> list(Page<Linkage> page, Linkage entity) {
        return this.linkageService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:linkage:edit"})
    public Result<Linkage> getById(Long id) {
        return this.linkageService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新场景联动记录")
    @CheckPermission(value = {"iot:linkage:edit", "iot:linkage:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Linkage entity) {
        return this.linkageService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除场景联动记录")
    @CheckPermission({"iot:linkage:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.linkageService.removeByIds(idList);
    }
}

