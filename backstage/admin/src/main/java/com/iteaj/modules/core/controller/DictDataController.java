package com.iteaj.modules.core.controller;

import com.iteaj.core.entity.DictData;
import com.iteaj.core.service.IDictDataService;
import com.iteaj.framework.BaseController;
import com.iteaj.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
@Controller
@RequestMapping("/core/dictData")
public class DictDataController extends BaseController {

    private final IDictDataService dictDataService;

    public DictDataController(IDictDataService dictDataService) {
        this.dictDataService = dictDataService;
    }

    @GetMapping()
    public String view(){
        return "dictData";
    }

    @ResponseBody
    @GetMapping("/view")
    public Result list(DictData dictData) {
        return dictDataService.selectList(dictData).build(success());
    }

    @ResponseBody
    @GetMapping("/listByType")
    public Result listByDictType(String type) {
        return dictDataService.selectByType(type).build(success());
    }

    @ResponseBody
    @PostMapping("/add")
    public Result add(@RequestBody DictData data) {
        return dictDataService.insert(data).build(success());
    }

    @ResponseBody
    @PostMapping("/del")
    public Result del(@RequestBody List<Long> list) {
        return dictDataService.deleteByIds(list).build(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    public Result edit(@RequestBody DictData data) {
        return dictDataService.updateById(data).build(success());
    }
}
