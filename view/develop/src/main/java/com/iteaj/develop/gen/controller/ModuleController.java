package com.iteaj.develop.gen.controller;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.iteaj.develop.entity.GenField;
import com.iteaj.develop.entity.GenModule;
import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.entity.dto.GenModuleDto;
import com.iteaj.develop.entity.dto.GenTableDto;
import com.iteaj.develop.gen.GenUtils;
import com.iteaj.develop.service.IGenModuleService;
import com.iteaj.develop.service.IGenTableService;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.spring.boot.config.DevelopProperties;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.result.DetailResult;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * create time: 2020/4/13
 *
 * @author iteaj
 * @since 1.0
 */
@Controller
@RequestMapping("/dev/module")
public class ModuleController extends BaseController {

    private final DevelopProperties properties;
    private final IGenTableService genTableService;
    private final IGenModuleService genModuleService;

    public ModuleController(IGenModuleService genModuleService, IGenTableService genTableService, DevelopProperties properties) {
        this.genModuleService = genModuleService;
        this.genTableService = genTableService;
        this.properties = properties;
    }

    @GetMapping
    public String view() {
        return "module";
    }

    @ResponseBody
    @GetMapping("/view")
    public Result view(GenModule module) {
        return this.genModuleService.selectList(module).response(success());
    }

    @ResponseBody
    @PostMapping("/add")
    public Result add(@RequestBody GenModule module) {
        return genModuleService.insert(module).response(success());
    }

    @ResponseBody
    @PostMapping("/edit")
    public Result edit(@RequestBody GenModule module) {
        return genModuleService.updateById(module).response(success("更新成功"));
    }

    /**
     * 导入表信息
     * @param module
     * @return
     */
    @ResponseBody
    @PostMapping("/import")
    public Result genTable(@RequestBody GenModule module) {
        ListResult<GenTable> existsTables = genTableService
                .selectList(new GenTable().setDbName(module.getDbName()));

        List<GenTable> genTables = GenUtils.importTablesAndFields(module, null);

        //对数据库的所有表和已经导入的表进行求余
        ArrayList subtract = (ArrayList)org.apache.commons.collections
                .CollectionUtils.subtract(genTables, existsTables.get());

        // 导入新增的表
        if(!CollectionUtils.isEmpty(subtract)) {
            return genTableService.insertAsBatch(subtract)
                    .response(success("成功导入表"+subtract.size()+"张"));
        } else {
            return success("成功导入表0张");
        }
    }

    /**
     * 查询指定模块下面的所有表
     * @param module
     * @return
     */
    @ResponseBody
    @GetMapping("/tables")
    public Result getTableName(GenModule module) {
        GenModule genModule = genModuleService.getById(module.getId()).get();
        List<String> dbAllTableName = GenUtils.getDbAllTableName(genModule);
        return new ListResult(dbAllTableName).response(success("查询成功"));
    }
}
