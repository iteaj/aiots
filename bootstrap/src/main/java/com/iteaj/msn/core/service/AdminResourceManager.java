package com.iteaj.msn.core.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.spi.admin.*;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.msn.core.entity.Menu;
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
public class AdminResourceManager implements ResourceManager{

    @Autowired
    private IMenuService menuService;
    @Autowired
    private IDictTypeService dictTypeService;

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
    public Collection<MenuResource> listResourcesByType(MenuType... types) {
        ListResult<Menu> listResult = menuService.list(Wrappers
                .<Menu>lambdaQuery().in(Menu::getType, types));

        Collection<Menu> menus = TreeUtils.toTrees(listResult.getData(), 0l);

        return menus.stream().map(item -> (MenuResource)item)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
