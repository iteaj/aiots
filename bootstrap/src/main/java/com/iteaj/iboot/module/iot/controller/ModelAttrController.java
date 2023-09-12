package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
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
    @CheckPermission({"iot:modelAttr:view"})
    public Result<IPage<ModelAttr>> list(Page<ModelAttr> page, ModelAttr entity) {
        return this.modelAttrService.page(page, entity);
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
    public Result<Boolean> saveOrUpdate(@RequestBody ModelAttr entity) {
        return this.modelAttrService.saveOrUpdate(entity);
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
}

