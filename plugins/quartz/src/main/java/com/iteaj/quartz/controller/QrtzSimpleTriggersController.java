package com.iteaj.quartz.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.quartz.entity.QrtzSimpleTriggers;
import com.iteaj.quartz.service.IQrtzSimpleTriggersService;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  控制器
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@Controller
@RequestMapping("/core/triggers")
public class QrtzSimpleTriggersController extends BaseController {

    @Autowired
    private IQrtzSimpleTriggersService qrtzSimpleTriggersService;

    @GetMapping
    public String view() {
        return "qrtzSimpleTriggers";
    }

    @ResponseBody
    @GetMapping("/view")
    public Result view(QrtzSimpleTriggers entity, Page page) {
        Page page1 = this.qrtzSimpleTriggersService.page(page, Wrappers.query(entity));
        return new PageResult(page1);
    }

//    @ResponseBody
//    @PostMapping("/add")
//    public Result add(@RequestBody QrtzSimpleTriggers entity) {
//        return this.qrtzSimpleTriggersService.save(entity).response(success("新增记录成功"));
//    }
//
//    @ResponseBody
//    @PostMapping("/edit")
//    public Result edit(@RequestBody QrtzSimpleTriggers entity) {
//        return this.qrtzSimpleTriggersService.updateById(entity).response(success("修改记录成功"));
//    }
//
//    @ResponseBody
//    @GetMapping("/detail")
//    public Result detail(Long id) {
//        return this.qrtzSimpleTriggersService.getById(id).response(success());
//    }
//
//    @ResponseBody
//    @PostMapping("/del")
//    public Result del(@RequestBody List<Long> ids) {
//        return this.qrtzSimpleTriggersService.removeByIds(ids).response(success("删除记录成功"));
//    }

}

