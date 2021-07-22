package com.iteaj.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.msn.core.entity.DictData;
import com.iteaj.msn.core.service.IDictDataService;
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
@RestController
@RequestMapping("/core/dictData")
public class DictDataController extends BaseController {

    private final IDictDataService dictDataService;

    public DictDataController(IDictDataService dictDataService) {
        this.dictDataService = dictDataService;
    }

    @ResponseBody
    @GetMapping("/view")
    public Result list(DictData dictData) {
        return dictDataService.selectList(dictData);
    }

    @GetMapping("/listByType")
    public Result<List<DictData>> listByDictType(String type) {
        return dictDataService.selectByType(type);
    }

    @PostMapping("/add")
    public Result add(@RequestBody DictData data) {
        return dictDataService.insert(data);
    }

    @PostMapping("/del")
    public Result del(@RequestBody List<Long> list) {
        return dictDataService.deleteByIds(list);
    }

    @GetMapping("/edit")
    public Result<DictData> edit(Long id) {
        return dictDataService.getById(id);
    }

    @PostMapping("/edit")
    public Result edit(@RequestBody DictData data) {
        return dictDataService.updateById(data);
    }
}
