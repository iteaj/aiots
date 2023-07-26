package com.iteaj.iboot.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.core.entity.Region;
import com.iteaj.iboot.msn.core.service.IRegionService;
import com.iteaj.framework.security.CheckPermission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 区域管理
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/region")
public class RegionController extends BaseController {

    private final IRegionService regionService;

    public RegionController(IRegionService regionService) {
        this.regionService = regionService;
    }

    /**
     * 获取区域列表
     * @param region
     * @return
     */
    @GetMapping("/view")
    @CheckPermission("core:region:view")
    public Result<List<Region>> view(Region region) {
        return this.regionService.list(region);
    }

    /**
     * 获取指定记录
     * @param id
     * @return
     */
    @ResponseBody
    @GetMapping("/getById")
    public Result<Region> getById(Long id) {
        return this.regionService.getById(id);
    }
}
