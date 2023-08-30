package com.iteaj.iboot.module.core.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.message.MessageManager;
import com.iteaj.framework.spi.message.MessageService;
import com.iteaj.iboot.module.core.entity.MessageSource;
import com.iteaj.iboot.module.core.service.IMessageSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    private final MessageManager messageManager;
    private final IMessageSourceService messageSourceService;

    public MessageSourceController(@Autowired(required = false) MessageManager messageManager
            , IMessageSourceService messageSourceService) {
        this.messageManager = messageManager;
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

    /**
     * 获取平台支持的消息类型
     * @return
     */
    @GetMapping("/types")
    public Result<List<IVOption>> types() {
        if(messageManager == null) {
            return success(Collections.emptyList());
        } else {
            List<IVOption> options = this.messageManager.getTypes().stream().map(type -> {
                List<MessageService> services = this.messageManager.getServices(type);
                MessageService messageService = services.get(0);

                IVOption ivOption = new IVOption(messageService.getName(), messageService.getType());
                services.forEach(channel -> ivOption.addChildren(channel.getChannelName()
                        , channel.getChannelId(), channel.getConfigParams()));

                return ivOption;
            }).collect(Collectors.toList());

            return success(options);
        }
    }
}

