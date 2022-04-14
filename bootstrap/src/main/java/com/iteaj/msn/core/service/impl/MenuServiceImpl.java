package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.spi.admin.MenuType;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.mapper.IMenuDao;
import com.iteaj.msn.core.service.IMenuService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends BaseServiceImpl<IMenuDao, Menu> implements IMenuService {

    @Override
    public BooleanResult save(Menu entity) {
        getOne(Wrappers.<Menu>lambdaQuery()
                .eq(Menu::getName, entity.getName())
                .eq(Menu::getType, MenuType.V))
                .ofNullable()
                .ifPresent(menu -> {throw new ServiceException("菜单名称重复");});
        return super.save(entity);
    }

    @Override
    public ListResult<Menu> selectMenuTrees(Menu menu) {
        // 只查询已经注册过的模块的菜单列表
        List<Menu> menuList = this.getBaseMapper() // 根据sort排序后的数据
                .selectList(Wrappers.query(menu).orderByAsc("sort"));

        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(menuList, 0l);
        return new ListResult<>(menus);
    }

    @Override
    public ListResult<Menu> selectMenuBarTrees(Long aid, boolean isSuper) {
        // 只查询已经注册过的模块的菜单列表
        List<Menu> allMenus = this.getBaseMapper()
                .selectList(Wrappers.<Menu>query()
                        .ne("status", PermStatus.disabled)
                        .orderByAsc("sort"));
        if(CollectionUtils.isEmpty(allMenus)) {
            return new ListResult<>(Collections.emptyList());
        }

        if(isSuper) { // 超级管理员拥有所有菜单
            // 转成树结构
            List<Menu> menus = (List<Menu>) TreeUtils.toTrees(allMenus, 0l);
            return new ListResult<>(menus);
        } else { // 普通管理员的菜单列表
            List<String> adminMenuIds = this.getBaseMapper().selectAdminMenus(aid);
            final Collection<Menu> menus = TreeUtils.toAdminMenuTrees(allMenus, adminMenuIds);
            return new ListResult<>(menus.stream().collect(Collectors.toList()));
        }
    }

    @Override
    public ListResult selectParentTrees() {
        // 去除禁用的状态
        List<Menu> status = this.getBaseMapper()
                .selectList(Wrappers.<Menu>query()
                        .ne("type", MenuType.A)
                        .ne("status", PermStatus.disabled));

        // 转成树结构
        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(status, 0l);
        return new ListResult(menus);
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            throw new ServiceException("未指定要删除的记录");
        }
        if(idList.size() > 1) {
            throw new ServiceException("菜单不支持批量删除");
        }

        idList.stream().forEach(item -> {
            this.count(Wrappers.<Menu>query().eq("pid", item))
                    .ofNullable().filter(count -> count > 0)
                    .ifPresent((menu) -> {
                        throw new ServiceException("请先删除子菜单");
                    });
        });

        return super.removeByIds(idList);
    }

    @Override
    public Menu getByUrl(String url) {
        return getBaseMapper().selectOne(Wrappers.<Menu>query().eq("url", url));
    }
}
