package com.iteaj.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.msn.core.entity.DictData;
import com.iteaj.msn.core.service.IDictDataService;
import com.iteaj.framework.result.Result;
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
        return dictDataService.list(dictData);
    }

    @GetMapping("/listByType")
    public Result<List<DictData>> listByDictType(String type) {
        return dictDataService.selectByType(type);
    }

    @PostMapping("/add")
    public Result add(@RequestBody DictData data) {
        return dictDataService.save(data);
    }

    @PostMapping("/del")
    public Result del(@RequestBody List<Long> list) {
        return dictDataService.removeByIds(list);
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
