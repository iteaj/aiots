package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.service.IDeviceGroupService;
import com.iteaj.framework.BaseController;

/**
 * 设备分组管理
 * @author iteaj
 */
@RestController
@RequestMapping("/iot/deviceGroup")
public class DeviceGroupController extends BaseController {

    private final IDeviceGroupService deviceGroupService;

    public DeviceGroupController(IDeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览设备分组功能")
    @CheckPermission({"iot:deviceGroup:view"})
    public Result<IPage<DeviceGroup>> list(Page<DeviceGroup> page, DeviceGroup entity) {
        return this.deviceGroupService.page(page, entity);
    }

    /**
     * 树结构分组列表
     * @param entity
     * @return
     */
    @GetMapping("/tree")
    public Result<List<DeviceGroup>> tree(DeviceGroup entity) {
        return this.deviceGroupService.tree(entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:deviceGroup:edit"})
    public Result<DeviceGroup> getById(Long id) {
        return this.deviceGroupService.detailById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新设备分组记录")
    @CheckPermission(value = {"iot:deviceGroup:edit", "iot:deviceGroup:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody DeviceGroup entity) {
        return this.deviceGroupService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除设备分组记录")
    @CheckPermission({"iot:deviceGroup:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceGroupService.removeByIds(idList);
    }
}

