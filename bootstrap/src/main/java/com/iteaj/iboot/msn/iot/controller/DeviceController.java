package com.iteaj.iboot.msn.iot.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.service.IDeviceService;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 设备管理
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
@RestController
@RequestMapping("/iot/device")
public class DeviceController extends BaseController {

    private final IDeviceService deviceService;

    public DeviceController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:device:view"})
    public Result<IPage<Device>> list(Page<Device> page, Device entity) {
        return this.deviceService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:device:edit"})
    public Result<Device> getEditDetail(Long id) {
        return this.deviceService.getById(id);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @RequiresPermissions({"iot:device:edit"})
    public Result<Boolean> edit(@RequestBody Device entity) {
        return this.deviceService.updateById(entity);
    }

    /**
    * 新增记录
    * @param entity
    */
    @PostMapping("/add")
    @RequiresPermissions({"iot:device:add"})
    public Result<Boolean> add(@RequestBody Device entity) {
        return this.deviceService.save(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:device:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceService.removeByIds(idList);
    }

    /**
     * 获取指定型号下面的设备列表
     * @param modelId
     * @return
     */
    @GetMapping("listByModel")
    public Result<List<Device>> listByModel(Long modelId) {
        return this.deviceService.list(Wrappers.<Device>lambdaQuery().eq(Device::getModel, modelId));
    }
}

