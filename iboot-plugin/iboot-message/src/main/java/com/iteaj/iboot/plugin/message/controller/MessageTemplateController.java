package com.iteaj.iboot.plugin.message.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.message.entity.MessageTemplate;
import com.iteaj.iboot.plugin.message.service.IMessageTemplateService;
import com.iteaj.framework.BaseController;

/**
 * 消息模板管理
 * @author iteaj
 * @since 2024-07-09
 */
@RestController
@RequestMapping("/message/messageTemplate")
public class MessageTemplateController extends BaseController {

    private final IMessageTemplateService messageTemplateService;

    public MessageTemplateController(IMessageTemplateService messageTemplateService) {
        this.messageTemplateService = messageTemplateService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览消息模板功能")
    @CheckPermission({"message:messageTemplate:view"})
    public Result<IPage<MessageTemplate>> list(Page<MessageTemplate> page, MessageTemplate entity) {
        return this.messageTemplateService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"message:messageTemplate:edit"})
    public Result<MessageTemplate> getById(Long id) {
        return this.messageTemplateService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新消息模板记录")
    @CheckPermission(value = {"message:messageTemplate:edit", "message:messageTemplate:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody MessageTemplate entity) {
        return this.messageTemplateService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除消息模板记录")
    @CheckPermission({"message:messageTemplate:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.messageTemplateService.removeByIds(idList);
    }
}

