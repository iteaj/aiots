package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品管理
 *
 * @author iteaj
 * @since 2022-07-22
 */
@RestController
@RequestMapping("/iot/product")
public class ProductController extends BaseController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @CheckPermission({"iot:product:view"})
    public Result<IPage<ProductDto>> list(Page<Product> page, Product entity) {
        return this.productService.pageOfDetail(page, entity);
    }

    /**
     * 获取指定类型下面的列表
     * @param typeId -1将获取所有
     * @return
     */
    @GetMapping("/listByType")
    public Result<List<Product>> listByType(Long typeId) {
        return this.productService.list(Wrappers
                .<Product>lambdaQuery()
                .eq(typeId == null || typeId != -1, Product::getProductTypeId, typeId));
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:product:edit"})
    public Result<Product> getById(Long id) {
        return this.productService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:product:edit", "iot:product:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Product entity) {
        return this.productService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:product:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.productService.removeByIds(idList);
    }

    /**
     * 设备类型列表
     * @return
     */
    @GetMapping("deviceTypes")
    public Result<List<IVOption>> deviceTypes() {
        return success(DeviceType.options());
    }
}

