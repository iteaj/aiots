package com.iteaj.iboot.msn.iot.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.DeviceType;
import com.iteaj.iboot.msn.iot.service.IDeviceTypeService;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 设备类型管理
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
@RestController
@RequestMapping("/iot/deviceType")
public class DeviceTypeController extends BaseController {

    private final IDeviceTypeService deviceTypeService;

    public DeviceTypeController(IDeviceTypeService deviceTypeService) {
        this.deviceTypeService = deviceTypeService;
    }

    /**
    * 列表查询
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:deviceType:view"})
    public Result<List<DeviceType>> list() {
        return this.deviceTypeService.tree();
    }

    @GetMapping("/tree")
    public Result<List<DeviceType>> tree() {
        return this.deviceTypeService.tree();
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:deviceType:edit"})
    public Result<DeviceType> getEditDetail(Long id) {
        return this.deviceTypeService.getById(id);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @RequiresPermissions({"iot:deviceType:edit"})
    public Result<Boolean> edit(@RequestBody DeviceType entity) {
        return this.deviceTypeService.updateById(entity);
    }

    /**
    * 新增记录
    * @param entity
    */
    @PostMapping("/add")
    @RequiresPermissions({"iot:deviceType:add"})
    public Result<Boolean> add(@RequestBody DeviceType entity) {
        return this.deviceTypeService.save(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:deviceType:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceTypeService.removeByIds(idList);
    }
}

