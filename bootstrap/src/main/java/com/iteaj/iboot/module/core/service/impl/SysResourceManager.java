package com.iteaj.iboot.module.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.spi.admin.*;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.iboot.module.core.entity.Config;
import com.iteaj.iboot.module.core.entity.Menu;
import com.iteaj.iboot.module.core.service.IConfigService;
import com.iteaj.iboot.module.core.service.IDictDataService;
import com.iteaj.iboot.module.core.service.IDictTypeService;
import com.iteaj.iboot.module.core.service.IMenuService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * create time: 2020/6/26
 *  后台系统资源管理
 * @author iteaj
 * @since 1.0
 */
public class SysResourceManager implements ResourceManager{

    @Resource
    private IMenuService menuService;
    @Resource
    private IConfigService configService;
    @Resource
    private IDictTypeService dictTypeService;
    @Resource
    private IDictDataService dictDataService;


    @Override
    public List<IVOption> getConfig(List<String> labels) {
        return configService.list(Wrappers.<Config>lambdaQuery().in(Config::getLabel, labels))
                .stream()
                .map(item -> new IVOption(item.getLabel(), item))
                .collect(Collectors.toList());
    }

    @Override
    public String getMsnByMenuId(Long menuId) {
        return menuService.getById(menuId)
                .ofNullable()
                .map(item -> item.getMsn())
                .orElse(null);
    }

    @Override
    public UrlResource getByUrl(String url) {
        return menuService.getByUrl(url);
    }

    @Override
    public List<DictResource> getDictResources() {
        return dictTypeService.list().stream().map(item ->
                (DictResource) item).collect(Collectors.toList());
    }

    @Override
    public List<IVOption> getDictData(String dictType) {
        return dictDataService.selectByType(dictType).stream()
                .map(item -> new IVOption(item.getLabel(), item.getValue()))
                .toList();
    }

    @Override
    public Collection<MenuResource> listResourcesByType(MenuType... types) {
        ListResult<Menu> listResult = menuService.list(Wrappers
                .<Menu>lambdaQuery().in(Menu::getType, types));

        Collection<Menu> menus = TreeUtils.toTrees(listResult.getData(), 0l);

        return menus.stream().map(item -> (MenuResource)item)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
