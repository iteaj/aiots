package com.iteaj.iboot.msn.iot.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.PointGroup;
import com.iteaj.iboot.msn.iot.service.IPointGroupService;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 点位组管理
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@RestController
@RequestMapping("/iot/pointGroup")
public class PointGroupController extends BaseController {

    private final IPointGroupService pointGroupService;

    public PointGroupController(IPointGroupService pointGroupService) {
        this.pointGroupService = pointGroupService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:pointGroup:view"})
    public Result<IPage<PointGroup>> list(Page<PointGroup> page, PointGroup entity) {
        return this.pointGroupService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:pointGroup:edit"})
    public Result<PointGroup> getById(Long id) {
        return this.pointGroupService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:pointGroup:edit", "iot:pointGroup:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody PointGroup entity) {
        return this.pointGroupService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:pointGroup:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.pointGroupService.removeByIds(idList);
    }
}

