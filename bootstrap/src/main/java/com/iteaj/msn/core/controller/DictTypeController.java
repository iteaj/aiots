package com.iteaj.msn.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.msn.core.entity.DictType;
import com.iteaj.msn.core.service.IDictTypeService;
import com.iteaj.util.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * jdk：1.8
 *  字典类型管理
 * @author iteaj
 * create time：2019/6/24
 */
@RestController
@RequestMapping("/core/dictType")
public class DictTypeController extends BaseController {

    private final IDictTypeService dictTypeService;

    public DictTypeController(IDictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    @GetMapping("/view")
    @RequiresPermissions("core:dictType:view")
    public Result<Page> list(DictType dictType, Page page) {
        return dictTypeService.selectByPage(page, dictType);
    }

    @GetMapping("/list")
    public Result<List<DictType>> list(DictType dictType) {
        return dictTypeService.selectList(dictType);
    }

    @PostMapping("/add")
    @RequiresPermissions("core:dictType:add")
    public Result<DictType> add(@RequestBody DictType dictType) {
        return dictTypeService.insert(dictType);
    }

    @PostMapping("/del")
    @RequiresPermissions("core:dictType:del")
    public Result<Boolean> del(@RequestBody List<Long> list) {
        return dictTypeService.deleteByIds(list);
    }

    @PostMapping("/edit")
    @RequiresPermissions("core:dictType:edit")
    public Result<Boolean> edit(@RequestBody DictType dictType) {
        return dictTypeService.updateById(dictType);
    }
}
