package com.iteaj.iboot.msn.iot.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.msn.iot.consts.TypeAliasOptions;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.msn.iot.entity.DeviceType;
import com.iteaj.iboot.msn.iot.service.IDeviceTypeService;
import com.iteaj.framework.BaseController;

/**
 * 设备类型管理
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
    @CheckPermission({"iot:deviceType:view"})
    public Result<List<DeviceType>> list(DeviceType entity) {
        return this.deviceTypeService.tree(entity);
    }

    @GetMapping("/tree")
    public Result<List<DeviceType>> tree(String alias) {
        DeviceType type = new DeviceType();
        type.setAlias(alias);
        return this.deviceTypeService.tree(type);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:deviceType:edit"})
    public Result<DeviceType> getEditDetail(Long id) {
        return this.deviceTypeService.getById(id);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @CheckPermission({"iot:deviceType:edit"})
    public Result<Boolean> edit(@RequestBody DeviceType entity) {
        return this.deviceTypeService.updateById(entity);
    }

    /**
    * 新增记录
    * @param entity
    */
    @PostMapping("/add")
    @CheckPermission({"iot:deviceType:add"})
    public Result<Boolean> add(@RequestBody DeviceType entity) {
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

