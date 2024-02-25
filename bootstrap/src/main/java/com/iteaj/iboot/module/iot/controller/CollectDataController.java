package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.CollectDataDto;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.service.ICollectDataService;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采集数据管理
 */
@RestController
@RequestMapping("/iot/collectData")
public class CollectDataController extends BaseController {

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private ICollectDataService collectDataService;

    /**
     * 获取采集数据
     * @param page
     * @param entity
     * @return
     */
    @GetMapping("/view")
    public Result<Page<CollectDataDto>> view(Page page, CollectDataDto entity) {
        return collectDataService.detailOfPage(page, entity);
    }

    /**
     * 新增或者更新记录
     * @param id
     */
    @GetMapping("/edit")
    public Result<CollectData> edit(Long id) {
        return this.collectDataService.getById(id);
    }

    /**
     * 删除指定记录
     * @param idList
     */
    @PostMapping("/del")
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.collectDataService.removeByIds(idList);
    }
}
