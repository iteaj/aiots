package com.iteaj.modules.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.core.entity.DictType;
import com.iteaj.core.service.IDictTypeService;
import com.iteaj.framework.BaseController;
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
@Controller
@RequestMapping("/core/dictType")
public class DictTypeController extends BaseController {

    private final IDictTypeService dictTypeService;

    public DictTypeController(IDictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    @GetMapping
    @RequiresPermissions("core:dictType:view")
    public String view() {
        return "dict";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:dictType:view")
    public Result list(DictType dictType, Page page) {
        return dictTypeService.selectByPage(page, dictType).build(success());
    }

    @ResponseBody
    @GetMapping("/list")
    public Result list(DictType dictType) {
        return dictTypeService.selectList(dictType).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    @RequiresPermissions("core:dictType:add")
    public Result add(@RequestBody DictType dictType) {
        return dictTypeService.insert(dictType).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:dictType:del")
    public Result del(@RequestBody List<Long> list) {
        return dictTypeService.deleteByIds(list).build(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("core:dictType:edit")
    public Result edit(@RequestBody DictType dictType) {
        return dictTypeService.updateById(dictType).build(success());
    }
}
