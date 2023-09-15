package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
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
@RequestMapping("/iot/deviceType")
public class ProductTypeController extends BaseController {

    private final IProductTypeService deviceTypeService;

    public ProductTypeController(IProductTypeService deviceTypeService) {
        this.deviceTypeService = deviceTypeService;
    }

    /**
    * 列表查询
    */
    @GetMapping("/view")
    @CheckPermission({"iot:deviceType:view"})
    public Result<List<ProductType>> list(ProductType entity) {
        return this.deviceTypeService.tree(entity);
    }

    @GetMapping("/tree")
    public Result<List<ProductType>> tree(String alias) {
        ProductType type = new ProductType();
        type.setAlias(alias);
        return this.deviceTypeService.tree(type);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:deviceType:edit"})
    public Result<ProductType> getEditDetail(Long id) {
        return this.deviceTypeService.getById(id);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @CheckPermission({"iot:deviceType:edit"})
    public Result<Boolean> edit(@RequestBody ProductType entity) {
        return this.deviceTypeService.updateById(entity);
    }

    /**
    * 新增记录
    * @param entity
    */
    @PostMapping("/add")
    @CheckPermission({"iot:deviceType:add"})
    public Result<Boolean> add(@RequestBody ProductType entity) {
        return this.deviceTypeService.save(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:deviceType:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceTypeService.removeByIds(idList);
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

