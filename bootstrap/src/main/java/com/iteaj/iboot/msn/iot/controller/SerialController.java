package com.iteaj.iboot.msn.iot.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.Serial;
import com.iteaj.iboot.msn.iot.service.ISerialService;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 串口设备管理
 * </p>
 *
 * @author iteaj
 * @since 2023-04-12
 */
@RestController
@RequestMapping("/iot/serial")
public class SerialController extends BaseController {

    private final ISerialService serialService;

    public SerialController(ISerialService serialService) {
        this.serialService = serialService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:serial:view"})
    public Result<IPage<Serial>> list(Page<Serial> page, Serial entity) {
        return this.serialService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:serial:edit"})
    public Result<Serial> getById(Long id) {
        return this.serialService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:serial:edit", "iot:serial:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Serial entity) {
        return this.serialService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:serial:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.serialService.removeByIds(idList);
    }
}

