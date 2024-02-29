package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.service.IWarnConfigService;
import com.iteaj.framework.BaseController;

/**
 * 告警配置管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/warnConfig")
public class WarnConfigController extends BaseController {

    private final IWarnConfigService warnConfigService;

    public WarnConfigController(IWarnConfigService warnConfigService) {
        this.warnConfigService = warnConfigService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览告警配置功能")
    @CheckPermission({"iot:warnConfig:view"})
    public Result<IPage<WarnConfig>> list(Page<WarnConfig> page, WarnConfig entity) {
        return this.warnConfigService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:warnConfig:edit"})
    public Result<WarnConfig> getById(Long id) {
        return this.warnConfigService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新告警配置记录")
    @CheckPermission(value = {"iot:warnConfig:edit", "iot:warnConfig:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody WarnConfig entity) {
        return this.warnConfigService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除告警配置记录")
    @CheckPermission({"iot:warnConfig:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.warnConfigService.removeByIds(idList);
    }
}

