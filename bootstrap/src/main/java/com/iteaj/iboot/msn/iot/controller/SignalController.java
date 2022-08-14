package com.iteaj.iboot.msn.iot.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.Signal;
import com.iteaj.iboot.msn.iot.service.ISignalService;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 寄存器点位管理
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@RestController
@RequestMapping("/iot/signal")
public class SignalController extends BaseController {

    private final ISignalService signalService;

    public SignalController(ISignalService signalService) {
        this.signalService = signalService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:signal:view"})
    public Result<IPage<Signal>> list(Page<Signal> page, Signal entity) {
        return this.signalService.page(page, entity);
    }

    /**
     * 获取指定型号下面的点位列表
     * @param modelId 型号id
     */
    @GetMapping("/listByModel")
    public Result<List<Signal>> listByModel(Integer modelId) {
        return this.signalService.list(new Signal(modelId));
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:signal:edit"})
    public Result<Signal> getById(Long id) {
        return this.signalService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:signal:edit", "iot:signal:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Signal entity) {
        return this.signalService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:signal:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.signalService.removeByIds(idList);
    }
}

