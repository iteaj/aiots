package com.iteaj.msn.core.controller;

import java.util.List;
import com.iteaj.framework.result.Result;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.msn.core.entity.MsgSend;
import com.iteaj.msn.core.service.IMsgSendService;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 消息管理
 * </p>
 *
 * @author iteaj
 * @since 2021-09-25
 */
@RestController
@RequestMapping("/core/msgSend")
public class MsgSendController extends BaseController {

    private final IMsgSendService msgSendService;

    public MsgSendController(IMsgSendService msgSendService) {
        this.msgSendService = msgSendService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions(value = {"core:msgSend:view"})
    public Result<IPage<MsgSend>> list(Page<MsgSend> page, MsgSend entity) {
        return this.msgSendService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions(value = {"core:msgSend:edit"})
    public Result<MsgSend> getEditDetail(Long id) {
        return this.msgSendService.getById(id);
    }

    /**
    * 修改记录
    * @param entity
    */
    @PostMapping("/edit")
    @RequiresPermissions(value = {"core:msgSend:edit"})
    public Result<Boolean> edit(@RequestBody MsgSend entity) {
        return this.msgSendService.updateById(entity);
    }

    /**
    * 新增或者修改记录
    * @param entity
    */
    @PostMapping("/add")
    @RequiresPermissions(value = {"core:msgSend:add"})
    public Result<Boolean> add(@RequestBody MsgSend entity) {
        return this.msgSendService.save(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions(value = {"core:msgSend:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.msgSendService.removeByIds(idList);
    }
}

