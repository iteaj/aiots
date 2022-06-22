package com.iteaj.iboot.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.framework.spi.admin.MenuType;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.iboot.msn.core.entity.Menu;
import com.iteaj.iboot.msn.core.enums.Status;
import com.iteaj.iboot.msn.core.mapper.IMenuDao;
import com.iteaj.iboot.msn.core.service.IMenuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends BaseServiceImpl<IMenuDao, Menu> implements IMenuService, InitializingBean {

    private Map<Long, Menu> idMenuMap = new HashMap<>(128);
    private Map<String, Menu> loggerMenuCache = new HashMap<>(128);

    @Override
    public BooleanResult save(Menu entity) {
        getOne(Wrappers.<Menu>lambdaQuery()
                .eq(Menu::getName, entity.getName())
                .eq(Menu::getPid, entity.getPid()))
                .ofNullable()
                .ifPresent(menu -> {throw new ServiceException("同级已经存在菜单名称["+entity.getName()+"]");});

        final BooleanResult booleanResult = super.save(entity);
        this.syncMenuToCache();
        return booleanResult;
    }

    @Override
    public BooleanResult updateById(Menu entity) {
        getOne(Wrappers.<Menu>lambdaQuery()
                .ne(Menu::getId, entity.getId())
                .eq(Menu::getPid, entity.getPid())
                .eq(Menu::getName, entity.getName()))
                .ofNullable().ifPresent(menu -> {throw new ServiceException("同级已经存在菜单名称["+entity.getName()+"]");});

        final BooleanResult booleanResult = super.updateById(entity);
        this.syncMenuToCache();
        return booleanResult;
    }

    @Override
    public ListResult<Menu> selectMenuTrees(Menu menu) {
        List<Menu> menuList = this.getBaseMapper() // 根据sort排序后的数据
                .selectList(Wrappers.query(menu).orderByAsc("sort"));

        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(menuList, menu.getPid());
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

        final BooleanResult booleanResult = super.removeByIds(idList);
        this.syncMenuToCache();
        return booleanResult;
    }

    @Override
    public Menu getByUrl(String url) {
        return getBaseMapper().selectOne(Wrappers.<Menu>query().eq("url", url));
    }

    @Override
    public Menu getByUrlFromCache(String url) {
        return this.loggerMenuCache.get(url);
    }

    @Override
    public Menu getByIdFromCache(Long id) {
        return this.idMenuMap.get(id);
    }

    @Override
    public List<String> selectPermissions(Long adminId) {
        if(ShiroUtil.isSuper(adminId)) {
            return this.getBaseMapper()
                    .selectList(Wrappers.<Menu>lambdaQuery()
                            .ne(Menu::getStatus, PermStatus.disabled)
                            .ne(Menu::getType, MenuType.M)).stream()
                    .filter(item -> StringUtils.hasText(item.getPerms()))
                    .map(item -> item.getPerms()).collect(Collectors.toList());
        } else {
            return this.getBaseMapper().selectPermissions(adminId);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.syncMenuToCache();
    }

    private void syncMenuToCache() {
        this.loggerMenuCache.clear();
        this.list(Wrappers.<Menu>lambdaQuery()
                .eq(Menu::getType, MenuType.A)
                .eq(Menu::getStatus, Status.enabled)).stream()
                .filter(item -> StringUtils.hasText(item.getUrl()))
                .forEach(item -> {
                    this.idMenuMap.put(item.getId(), item);
                    this.loggerMenuCache.put(item.getUrl(), item);
                });
    }
}
