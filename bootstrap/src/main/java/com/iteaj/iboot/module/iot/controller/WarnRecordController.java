package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.spi.iot.data.CollectWarnDataService;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 告警记录管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/warnRecord")
public class WarnRecordController extends BaseController {

    private final CollectWarnDataService warnRecordService;

    public WarnRecordController(CollectWarnDataService warnRecordService) {
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
        return (Result<IPage<WarnRecord>>) this.warnRecordService.detailPage(page, entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除告警记录记录")
    @CheckPermission({"iot:warnRecord:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.warnRecordService.batchRemove(idList);
    }
}

