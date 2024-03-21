package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 设备管理
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
    @CheckPermission({"iot:device:view"})
    public Result<IPage<DeviceDto>> list(Page<Device> page, DeviceDto entity) {
        return this.deviceService.pageOfDetail(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:device:edit"})
    public Result<DeviceDto> getEditDetail(Long id) {
        return this.deviceService.detailById(id);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @CheckPermission({"iot:device:edit"})
    public Result<Boolean> edit(@RequestBody Device entity) {
        return this.deviceService.updateById(entity);
    }

    /**
    * 新增记录
    * @param entity
    */
    @PostMapping("/add")
    @CheckPermission({"iot:device:add"})
    public Result<Boolean> add(@RequestBody Device entity) {
        return this.deviceService.save(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:device:del"})
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
        return this.deviceService.list(Wrappers.<Device>lambdaQuery().eq(Device::getProductId, modelId));
    }

    /**
     * 获取网关设备
     * @param gatewayId 网关id
     * @return
     */
    @GetMapping("listOfGateway")
    public Result<List<Device>> listOfGateway(Long gatewayId) {
        return this.deviceService.listOfGateway(gatewayId);
    }

    /**
     * 获取指定产品列表下的所有设备
     * @param productIds
     * @return
     */
    @GetMapping("listOfProducts")
    public Result<List<Device>> listOfProducts(List<Long> productIds) {
        if(CollectionUtil.isEmpty(productIds)) {
            return success(Collections.EMPTY_LIST);
        }

        return this.deviceService.list(Wrappers.<Device>lambdaQuery().in(Device::getProductId, productIds));
    }
}

