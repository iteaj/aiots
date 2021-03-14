package com.iteaj.modules.dev.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.modules.dev.entity.GenField;
import com.iteaj.modules.dev.service.IGenFieldService;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2020/4/28
 *
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/dev/field")
public class GenFieldController extends BaseController {

    @Autowired
    private IGenFieldService genFieldService;

    @GetMapping
    public String view() {
        return "field";
    }

    @ResponseBody
    @GetMapping("/view")
    public Result<GenField> view(GenField field) {
        return this.genFieldService.selectList(field).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody GenField field) {
        return genFieldService.insert(field).build(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    public Result<Boolean> edit(@RequestBody GenField field) {
        return genFieldService.updateById(field).build(success());
    }

    /**
     * 代码生成
     * @param ids
     * @return
     */
    @ResponseBody
    @PostMapping("/gen")
    public Result gen(@RequestBody List<Long> ids) {
        if(CommonUtils.isNotEmpty(ids)) {
            return success();
        } else {
            return fail("请选择要生成的表");
        }
    }
}
