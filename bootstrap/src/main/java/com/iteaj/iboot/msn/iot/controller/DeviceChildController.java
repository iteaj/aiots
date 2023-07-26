package com.iteaj.iboot.msn.iot.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.DeviceChild;
import com.iteaj.iboot.msn.iot.service.IDeviceChildService;
import com.iteaj.framework.BaseController;

/**
 * 子设备管理
 *
 * @author iteaj
 * @since 2022-06-18
 */
@RestController
@RequestMapping("/iot/deviceChild")
public class DeviceChildController extends BaseController {

    private final IDeviceChildService deviceChildService;

    public DeviceChildController(IDeviceChildService deviceChildService) {
        this.deviceChildService = deviceChildService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    public Result<IPage<DeviceChild>> list(Page<DeviceChild> page, DeviceChild entity) {
        return this.deviceChildService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    public Result<DeviceChild> getById(Long id) {
        return this.deviceChildService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    public Result<Boolean> save(@RequestBody DeviceChild entity) {
        return this.deviceChildService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceChildService.removeByIds(idList);
    }

    /**
     * 或者指定设备下的子设备列表
     * @param uid 设备uid
     */
    @GetMapping("/listByUid")
    public Result<List<DeviceChild>> listByUid(Long uid) {
        return this.deviceChildService.list(Wrappers.<DeviceChild>lambdaQuery().eq(DeviceChild::getUid, uid));
    }
}

