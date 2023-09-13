package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.DeviceModel;
import com.iteaj.iboot.module.iot.service.IDeviceModelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品管理
 *
 * @author iteaj
 * @since 2022-07-22
 */
@RestController
@RequestMapping("/iot/deviceModel")
public class DeviceModelController extends BaseController {

    private final IDeviceModelService deviceModelService;

    public DeviceModelController(IDeviceModelService deviceModelService) {
        this.deviceModelService = deviceModelService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @CheckPermission({"iot:deviceModel:view"})
    public Result<IPage<ProductDto>> list(Page<DeviceModel> page, DeviceModel entity) {
        return this.deviceModelService.detailOfPage(page, entity);
    }

    /**
     * 获取指定类型下面的列表
     * @param typeId -1将获取所有
     * @return
     */
    @GetMapping("/listByType")
    public Result<List<DeviceModel>> listByType(Long typeId) {
        return this.deviceModelService.list(Wrappers
                .<DeviceModel>lambdaQuery()
                .eq(typeId != -1, DeviceModel::getTypeId, typeId));
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:deviceModel:edit"})
    public Result<DeviceModel> getById(Long id) {
        return this.deviceModelService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:deviceModel:edit", "iot:deviceModel:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody DeviceModel entity) {
        return this.deviceModelService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:deviceModel:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceModelService.removeByIds(idList);
    }
}

