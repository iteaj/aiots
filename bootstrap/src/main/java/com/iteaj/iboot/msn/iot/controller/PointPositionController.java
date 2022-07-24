package com.iteaj.iboot.msn.iot.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.Logical;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.iot.entity.PointPosition;
import com.iteaj.iboot.msn.iot.service.IPointPositionService;
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
@RequestMapping("/iot/pointPosition")
public class PointPositionController extends BaseController {

    private final IPointPositionService pointPositionService;

    public PointPositionController(IPointPositionService pointPositionService) {
        this.pointPositionService = pointPositionService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:pointPosition:view"})
    public Result<IPage<PointPosition>> list(Page<PointPosition> page, PointPosition entity) {
        return this.pointPositionService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:pointPosition:edit"})
    public Result<PointPosition> getById(Long id) {
        return this.pointPositionService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:pointPosition:edit", "iot:pointPosition:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody PointPosition entity) {
        return this.pointPositionService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:pointPosition:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.pointPositionService.removeByIds(idList);
    }
}

