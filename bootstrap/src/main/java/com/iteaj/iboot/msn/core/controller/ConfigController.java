package com.iteaj.iboot.msn.core.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.core.entity.Config;
import com.iteaj.iboot.msn.core.service.IConfigService;
import com.iteaj.framework.security.CheckPermission;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置管理
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/config")
public class ConfigController extends BaseController {

    private final IConfigService configService;

    public ConfigController(IConfigService configService) {
        this.configService = configService;
    }

    /**
     * 查询配置列表
     * @param page
     * @param config
     * @return
     */
    @GetMapping("/view")
    @CheckPermission("core:config:view")
    public Result<IPage<Config>> view(Page page, Config config) {
        return this.configService.page(page.addOrder(OrderItem.desc("create_time")), config);
    }

    /**
     * 查询配置列表
     * @param config
     * @return
     */
    @GetMapping("/list")
    public Result<List<Config>> list(Config config) {
        return this.configService.list(config);
    }

    /**
     * 新增配置
     * @param config
     * @return
     */
    @PostMapping("/add")
    @CheckPermission("core:config:add")
    public Result<Boolean> add(@RequestBody Config config) {
        return this.configService.save(config);
    }

    /**
     * 获取编辑详情
     * @param id
     * @return
     */
    @GetMapping("/edit")
    public Result<Config> edit(Long id) {
        return this.configService.getById(id);
    }

    /**
     * 修改配置
     * @param config
     * @return
     */
    @PostMapping("/edit")
    @CheckPermission("core:config:edit")
    public Result<Boolean> edit(@RequestBody Config config) {
        return this.configService.updateById(config);
    }

    /**
     * 删除记录
     * @param list
     * @return
     */
    @PostMapping("/del")
    @CheckPermission("core:config:del")
    public Result<Boolean> del(@RequestBody List<Long> list) {
        return this.configService.removeByIds(list);
    }
}
