package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import com.iteaj.framework.BaseController;

/**
 * 告警记录管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/warnRecord")
public class WarnRecordController extends BaseController {

    private final IWarnRecordService warnRecordService;

    public WarnRecordController(IWarnRecordService warnRecordService) {
        this.warnRecordService = warnRecordService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览告警记录功能")
    @CheckPermission({"iot:warnRecord:view"})
    public Result<IPage<WarnRecord>> list(Page<WarnRecord> page, WarnRecord entity) {
        return this.warnRecordService.detailPage(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:warnRecord:edit"})
    public Result<WarnRecord> getById(Long id) {
        return this.warnRecordService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新告警记录记录")
    @CheckPermission(value = {"iot:warnRecord:edit", "iot:warnRecord:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody WarnRecord entity) {
        return this.warnRecordService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除告警记录记录")
    @CheckPermission({"iot:warnRecord:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.warnRecordService.removeByIds(idList);
    }
}

