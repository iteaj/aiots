package com.iteaj.iboot.module.iot.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.entity.ModelAttrDict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.ModelAttr;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import com.iteaj.framework.BaseController;

/**
 * 物模型属性管理
 * @author iteaj
 * @since 2023-09-12
 */
@RestController
@RequestMapping("/iot/modelAttr")
public class ModelAttrController extends BaseController {

    private final IModelAttrService modelAttrService;

    public ModelAttrController(IModelAttrService modelAttrService) {
        this.modelAttrService = modelAttrService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览物模型属性功能")
//    @CheckPermission({"iot:modelAttr:view"})
    public Result<IPage<ModelAttr>> view(Page<ModelAttr> page, ModelAttr entity) {
        return this.modelAttrService.page(page, entity);
    }

    /**
     * 物模型属性字典列表查询
     * @param entity 搜索条件
     */
    @GetMapping("/dict/view")
    public Result<List<ModelAttrDict>> view(ModelAttrDict entity) {
        return this.modelAttrService.listModelAttrDicts(entity);
    }

    /**
     * 获取物模型属性列表
     * @param entity
     * @return
     */
    @GetMapping("/list")
    public Result<List<ModelAttr>> list(ModelAttr entity) {
        return this.modelAttrService.list(Wrappers.lambdaQuery(entity));
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:modelAttr:edit"})
    public Result<ModelAttr> getById(Long id) {
        return this.modelAttrService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新物模型属性记录")
    @CheckPermission(value = {"iot:modelAttr:edit", "iot:modelAttr:add"}, logical = Logical.OR)
    public Result<Long> saveOrUpdate(@Validated @RequestBody ModelAttr entity) {
        this.modelAttrService.saveOrUpdate(entity);
        return success(entity.getId());
    }

    /**
     * 新增或者更新记录
     * @param entity
     */
    @PostMapping("/dict/saveOrUpdate")
    @Logger("新增或者更新物模型属性记录")
    @CheckPermission(value = {"iot:modelAttr:edit", "iot:modelAttr:add"}, logical = Logical.OR)
    public Result<Boolean> dictSaveOrUpdate(@Validated @RequestBody ModelAttrDict entity) {
        return this.modelAttrService.saveOrUpdateModelAttrDict(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除物模型属性记录")
    @CheckPermission({"iot:modelAttr:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.modelAttrService.removeByIds(idList);
    }

    /**
     * 删除指定物模型字典记录
     * @param idList
     */
    @PostMapping("/dict/del")
    @Logger("删除物模型属性字典记录")
    public Result<Boolean> removeDict(@RequestBody List<Long> idList) {
        return this.modelAttrService.removeModelAttrDictByIds(idList);
    }
}

