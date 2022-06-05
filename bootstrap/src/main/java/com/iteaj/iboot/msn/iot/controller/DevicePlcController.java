package com.iteaj.iboot.msn.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.service.IDeviceService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * plc设备管理
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
@RestController
@RequestMapping("/iot/plc")
public class DevicePlcController extends BaseController {

    private final IDeviceService deviceService;

    public DevicePlcController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:plc:view"})
    public Result<IPage<Device>> list(Page<Device> page, DeviceDto entity) {
        entity.setAlias(DeviceTypeAlias.PLC);
        return this.deviceService.pageOfDetail(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:plc:edit"})
    public Result<Device> getEditDetail(Long id) {
        return this.deviceService.getById(id);
    }

    /**
     * 新增或者修改记录
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:plc:edit", "iot:plc:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Device entity) {
        return this.deviceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:plc:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceService.removeByIds(idList);
    }
}

