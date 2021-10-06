package com.iteaj.msn.core.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.msn.core.entity.Config;
import com.iteaj.msn.core.service.IConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * create time: 2019/11/27
 *  系统配置管理
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
    @RequiresPermissions("core:config:view")
    public Result<IPage<Config>> view(Page page, Config config) {
        return this.configService.page(page.addOrder(OrderItem.desc("create_time")), config);
    }

    /**
     * 新增配置
     * @param config
     * @return
     */
    @PostMapping("/add")
    @RequiresPermissions("core:config:add")
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
    @RequiresPermissions("core:config:edit")
    public Result<Boolean> edit(@RequestBody Config config) {
        return this.configService.updateById(config);
    }

    /**
     * 删除记录
     * @param list
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions("core:config:del")
    public Result<Boolean> del(@RequestBody List<Long> list) {
        return this.configService.removeByIds(list);
    }
}
