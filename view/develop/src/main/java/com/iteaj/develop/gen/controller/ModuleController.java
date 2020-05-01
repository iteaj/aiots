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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static String ParentPackageName = "com.iteaj";
    private static String titleRegex = "[\\s（($#@&,，。.].*";
    private static String SQLDriver = "com.mysql.cj.jdbc.Driver";
    private static String SQLUrl = "jdbc:mysql://localhost/%s?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT";

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
        return genModuleService.updateById(module).response(success());
    }

    /**
     * 导入表信息
     * @param module
     * @return
     */
    @ResponseBody
    @PostMapping("/import")
    public Result genTable(@RequestBody GenModule module) {
        GenModuleDto genModuleDto = this.genModuleService.joinGenTableById(module.getId());
        List<GenTable> tables = genModuleDto.getTables();
        if(CollectionUtils.isEmpty(tables)) {
            List<GenTable> genTables = GenUtils.importTablesAndFields(module, null);
            return genTableService.insertAsBatch(genTables).response(success("成功导入表"+genTables.size()+"张"));
        } else {
            return fail("此模块数据已经导入");
        }
    }

    /**
     * 生成代码
     * @param module
     * @return
     */
    @ResponseBody
    @PostMapping("/gen")
    public Result gen(@RequestBody GenModule module) {
        // 生成表和表字段信息

//        genTableService.insertAsBatch(genTables);
        return success();
    }
}
