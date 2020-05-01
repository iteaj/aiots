package com.iteaj.develop.gen.controller;

import com.iteaj.develop.entity.GenModule;
import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.entity.dto.GenTableDto;
import com.iteaj.develop.enums.GenStatus;
import com.iteaj.develop.gen.GenUtils;
import com.iteaj.develop.service.IGenModuleService;
import com.iteaj.develop.service.IGenTableService;
import com.iteaj.framework.BaseController;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/dev/table")
public class GenTableController extends BaseController {

    @Autowired
    private IGenTableService genTableService;
    @Autowired
    private IGenModuleService genModuleService;

    @GetMapping
    public String view() {
        return "tables";
    }

    @ResponseBody
    @GetMapping("/view")
    public Result view(GenTable table) {
        return this.genTableService.selectList(table).response(success());
    }

    @ResponseBody
    @PostMapping("/add")
    public Result add(@RequestBody GenTable table) {
        return genTableService.insert(table).response(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    public Result edit(@RequestBody GenTable table) {
        return genTableService.updateById(table).response(success());
    }

    @ResponseBody
    @PostMapping("/del")
    public Result del(@RequestBody List<Long> ids) {
        return genTableService.deleteByIds(ids).response(success());
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
            List<GenTableDto> genTables = genTableService.selectByJoinFields(ids);
            if(CollectionUtils.isEmpty(genTables)) {
                return fail("查找不到要生成的表信息");
            } else {
                String msn = genTables.get(0).getMsn();
                GenModule module = genModuleService.getByMsn(msn);
                if(module == null) {
                    return fail("查找不到表信息对应的模块");
                } else {
                    GenUtils.GenCode(module, genTables);
                    genTableService.updateStatusByBatch(genTables, GenStatus.已生成);
                }
            }
            return success();
        } else {
            return fail("请选择要生成的表");
        }
    }

    /**
     * 重新导入表
     * @param table
     * @return
     */
    @ResponseBody
    @PostMapping("/import")
    public Result reimport(@RequestBody GenTable table) {
        GenModule module = genModuleService.getByMsn(table.getMsn());
        if(null == module) return fail("查询不到此表所属的模块");

        List<GenTable> genTables = GenUtils.importTablesAndFields(module, Arrays.asList(table));
        genTableService.reImportTables(genTables);
        return success("重新导入表配置成功");
    }
}
