package com.iteaj.develop.gen.controller;

import com.iteaj.develop.entity.GenField;
import com.iteaj.develop.entity.GenModule;
import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.entity.dto.GenTableDto;
import com.iteaj.develop.enums.GenStatus;
import com.iteaj.develop.gen.GenUtils;
import com.iteaj.develop.service.IGenFieldService;
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
import java.util.stream.Collectors;

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
    private IGenFieldService genFieldService;
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
     * @return
     */
    @ResponseBody
    @PostMapping("/gen")
    public Result gen(@RequestBody List<GenTableDto> genTables) {
        if(CommonUtils.isNotEmpty(genTables)) {
            GenTableDto genTableDto = genTables.get(0);
            // 过滤掉禁用的
            genTables = genTables.stream().filter(item->
                    !item.getDisabled()).collect(Collectors.toList());

            // 生成单表信息, 会指定要生成文件
            if(genTables.size() == 1) {
                List<GenField> genFields = genFieldService
                        .selectByDbNameAndTableName(genTableDto
                                .getDbName(), genTableDto.getName());

                genTableDto.setFieldList(genFields);
            } else { // 批量生成表信息
                List<Long> list = genTables.stream().map(item ->
                        item.getId()).collect(Collectors.toList());

                genTables = genTableService.selectByJoinFields(list);
            }

            String msn = genTableDto.getMsn();
            GenModule module = genModuleService.getByMsn(msn);
            if(module == null) {
                return fail("查找不到表信息对应的模块");
            } else {
                GenUtils.GenCode(module, genTables);

                genTableService.updateStatusByBatch(genTables, GenStatus.已生成);
            }
            return success("生成表信息("+genTables.size()+"张)");
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
