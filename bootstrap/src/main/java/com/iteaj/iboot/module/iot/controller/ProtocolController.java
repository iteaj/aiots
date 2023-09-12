package com.iteaj.iboot.module.iot.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.Protocol;
import com.iteaj.iboot.module.iot.service.IProtocolService;
import com.iteaj.framework.BaseController;

/**
 * 报文协议管理
 * @author iteaj
 * @since 2023-09-12
 */
@RestController
@RequestMapping("/iot/protocol")
public class ProtocolController extends BaseController {

    private final IProtocolService protocolService;

    public ProtocolController(IProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览报文协议功能")
    @CheckPermission({"iot:protocol:view"})
    public Result<IPage<Protocol>> list(Page<Protocol> page, Protocol entity) {
        return this.protocolService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:protocol:edit"})
    public Result<Protocol> getById(Long id) {
        return this.protocolService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新报文协议记录")
    @CheckPermission(value = {"iot:protocol:edit", "iot:protocol:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Protocol entity) {
        return this.protocolService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除报文协议记录")
    @CheckPermission({"iot:protocol:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.protocolService.removeByIds(idList);
    }
}

