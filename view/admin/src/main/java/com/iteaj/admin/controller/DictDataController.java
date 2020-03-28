package com.iteaj.admin.controller;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.iteaj.admin.entity.DictData;
import com.iteaj.admin.service.IDictDataService;
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

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private IDictDataService dictDataService;

    @GetMapping()
    public String view(){
        return "dictData";
    }

    @ResponseBody
    @GetMapping("/view")
    public Result list(DictData dictData) {
        return dictDataService.selectList(dictData).response(success());
    }

    @ResponseBody
    @GetMapping("/listByType")
    public Result listByDictType(String type) {
        return dictDataService.selectByType(type).response(success());
    }

    @ResponseBody
    @PostMapping("/add")
    public Result add(@RequestBody DictData data) {
        return dictDataService.insert(data).response(success());
    }

    @ResponseBody
    @PostMapping("/del")
    public Result del(@RequestBody List<Long> list) {
        return dictDataService.deleteByIds(list).response(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    public Result edit(@RequestBody DictData data) {
        return dictDataService.updateById(data)
                .ifFail(fail()).response(success());
    }
}
