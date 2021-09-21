package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.enums.MenuType;
import com.iteaj.msn.core.mapper.IMenuDao;
import com.iteaj.msn.core.service.IMenuService;
import com.iteaj.util.CommonUtils;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class MenuServiceImpl extends BaseServiceImpl<IMenuDao, Menu> implements IMenuService {

    @Override
    public BooleanResult save(Menu entity) {
        try {
            return super.save(entity);
        } catch (Exception e) {
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ServiceException("菜单名称重复");
            } else {
                throw new ServiceException(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<Menu> selectBySuperAndMsn(String msn) {
        return getBaseMapper().selectList(new QueryWrapper<Menu>()
                .eq("msn", msn).ne("status", "disabled"));
    }

    @Override
    public ListResult<Menu> selectMenuTrees(Menu menu) {
        return this.selectMenuTreesInModules(menu, Collections.EMPTY_LIST);
    }

    @Override
    public ListResult<Menu> selectMenuTreesInModules(Menu menu, List<Module> modules) {
        // 获取已经注册的模块
        String[] msn = modules.stream().map(item -> item.getMsn()).toArray(String[]::new);

        // 只查询已经注册过的模块的菜单列表
        List<Menu> menuList = this.getBaseMapper() // 根据sort排序后的数据
                .selectList(Wrappers.query(menu)
                        .in(CommonUtils.isNotEmpty(msn), "msn", msn)
                        .orderByAsc("sort"));

        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(menuList, 0l);
        return new ListResult<>(menus);
    }

    @Override
    public ListResult<Menu> selectMenuBarTrees(Long aid, boolean isSuper) {
        return this.selectMenuBarTreesInModules(aid, Collections.EMPTY_LIST, isSuper);
    }

    @Override
    public ListResult<Menu> selectMenuBarTreesInModules(Long aid, List<Module> modules, boolean isSuper) {
        // 获取已经注册的模块
        String[] msn = modules.stream().map(item -> item.getMsn()).toArray(String[]::new);

        // 只查询已经注册过的模块的菜单列表
        List<Menu> allMenus = this.getBaseMapper()
                .selectList(Wrappers.<Menu>query()
                        .ne("status", PermStatus.disabled)
                        .in(CommonUtils.isNotEmpty(msn), "msn", msn)
                        .orderByAsc("sort"));

        if(isSuper) { // 超级管理员拥有所有菜单
            // 转成树结构
            List<Menu> menus = (List<Menu>) TreeUtils.toTrees(allMenus, 0l);
            return new ListResult<>(menus);
        } else { // 普通管理员的菜单列表
            List<String> adminMenuIds = this.getBaseMapper().selectAdminMenus(aid);
            return new ListResult<>((List<Menu>) TreeUtils.toAdminMenuTrees(allMenus, adminMenuIds));
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

        this.getOne(Wrappers.<Menu>query()
                .eq("pid", idList.stream().findFirst().get()))
                .ofNullable().ifPresent((item) -> {throw new ServiceException("请先删除子菜单");});

        return super.removeByIds(idList);
    }

    @Override
    public Menu getByUrl(String url) {
        return getBaseMapper().selectOne(Wrappers.<Menu>query().eq("url", url));
    }
}
