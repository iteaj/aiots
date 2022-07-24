package com.iteaj.iboot.msn.iot.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.DeviceModel;
import com.iteaj.iboot.msn.iot.service.IDeviceModelService;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 设备型号管理
 * </p>
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
    @RequiresPermissions({"iot:deviceModel:view"})
    public Result<IPage<DeviceModel>> list(Page<DeviceModel> page, DeviceModel entity) {
        return this.deviceModelService.page(page, entity);
    }

    /**
     * 通过设备类型获取设备型号列表
     * @param typeId -1将获取所有
     * @return
     */
    @GetMapping("/listByType")
    public Result<List<DeviceModel>> listByType(Integer typeId) {
        return this.deviceModelService.list(Wrappers
                .<DeviceModel>lambdaQuery()
                .eq(typeId != -1, DeviceModel::getTypeId, typeId));
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:deviceModel:edit"})
    public Result<DeviceModel> getById(Long id) {
        return this.deviceModelService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:deviceModel:edit", "iot:deviceModel:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody DeviceModel entity) {
        return this.deviceModelService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:deviceModel:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceModelService.removeByIds(idList);
    }
}

