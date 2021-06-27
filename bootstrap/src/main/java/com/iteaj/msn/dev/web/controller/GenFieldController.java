package com.iteaj.msn.dev.web.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.msn.dev.entity.GenField;
import com.iteaj.msn.dev.service.IGenFieldService;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "代码生成器-字段")
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
    @ApiOperation("浏览字段")
    public Result<GenField> view(GenField field) {
        return this.genFieldService.selectList(field).build(success());
    }

    @ResponseBody
    @ApiOperation("新增字段")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody GenField field) {
        return genFieldService.insert(field).build(success());
    }

    @ResponseBody
    @ApiOperation("编辑字段")
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
    @ApiOperation("生成字段")
    @PostMapping("/gen")
    public Result gen(@RequestBody List<Long> ids) {
        if(CommonUtils.isNotEmpty(ids)) {
            return success();
        } else {
            return fail("请选择要生成的表");
        }
    }
}
