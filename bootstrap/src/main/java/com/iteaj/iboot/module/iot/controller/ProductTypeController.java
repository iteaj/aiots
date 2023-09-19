package com.iteaj.iboot.module.iot.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.module.iot.consts.TypeAliasOptions;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.module.iot.entity.ProductType;
import com.iteaj.iboot.module.iot.service.IProductTypeService;
import com.iteaj.framework.BaseController;

/**
 * 产品类型管理
 *
 * @author iteaj
 * @since 2022-05-15
 */
@RestController
@RequestMapping("/iot/productType")
public class ProductTypeController extends BaseController {

    private final IProductTypeService productTypeService;

    public ProductTypeController(IProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    /**
    * 分页列表查询
    */
    @GetMapping("/view")
    @CheckPermission({"iot:productType:view"})
    public Result<IPage<ProductType>> list(Page<ProductType> page, ProductType entity) {
        return this.productTypeService.pageOfDetail(page, entity);
    }

    /**
     * 树结构列表
     * @param entity
     * @return
     */
    @GetMapping("/tree")
    public Result<List<ProductType>> tree(ProductType entity) {
        return this.productTypeService.tree(entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:productType:edit"})
    public Result<ProductType> getEditDetail(Long id) {
        return this.productTypeService.getById(id);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @CheckPermission({"iot:productType:edit"})
    public Result<Boolean> edit(@RequestBody ProductType entity) {
        return this.productTypeService.updateById(entity);
    }

    /**
    * 新增记录
    * @param entity
    */
    @PostMapping("/add")
    @CheckPermission({"iot:productType:add"})
    public Result<Boolean> add(@RequestBody ProductType entity) {
        return this.productTypeService.save(entity);
    }

    /**
     * 新增或者修改
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:productType:add", "iot:productType:edit"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody ProductType entity) {
        return this.productTypeService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:productType:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.productTypeService.removeByIds(idList);
    }

    /**
     * 获取设备类型别名列表
     * @return
     */
    @GetMapping("alias")
    public Result<List<TypeAliasOptions>> alias() {
        return success(DeviceTypeAlias.options());
    }
}

