package com.iteaj.iboot.msn.core.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.msn.core.entity.MessageSource;
import com.iteaj.iboot.msn.core.service.IMessageSourceService;
import com.iteaj.framework.BaseController;

/**
 * 消息源管理
 *
 * @author iteaj
 * @since 2023-07-30
 */
@RestController
@RequestMapping("/core/messageSource")
public class MessageSourceController extends BaseController {

    private final IMessageSourceService messageSourceService;

    public MessageSourceController(IMessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @Logger("浏览消息源功能")
    @GetMapping("/view")
    @CheckPermission({"core:messageSource:view"})
    public Result<IPage<MessageSource>> list(Page<MessageSource> page, MessageSource entity) {
        return this.messageSourceService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"core:messageSource:edit"})
    public Result<MessageSource> getById(Long id) {
        return this.messageSourceService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @Logger("新增或者更新消息源记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"core:messageSource:edit", "core:messageSource:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody MessageSource entity) {
        return this.messageSourceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @Logger("删除消息源记录")
    @PostMapping("/del")
    @CheckPermission({"core:messageSource:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.messageSourceService.removeByIds(idList);
    }
}

