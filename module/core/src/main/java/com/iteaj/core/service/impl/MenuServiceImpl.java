package com.iteaj.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.core.mapper.IMenuDao;
import com.iteaj.core.entity.Menu;
import com.iteaj.core.enums.MenuType;
import com.iteaj.core.service.IMenuService;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.consts.Position;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.result.BooleanResult;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class MenuServiceImpl extends BaseServiceImpl<IMenuDao, Menu> implements IMenuService {

    @Autowired
    private ResourceManager resourceManager;
    private Map<String, Menu> urlMenuMap = new HashMap<>(128);

    @Override
    public BooleanResult insert(Menu entity) {
        try {
            return super.insert(entity);
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
        return getBaseDao().selectList(new QueryWrapper<Menu>()
                .eq("msn", msn).ne("status", "disabled"));
    }

    @Override
    public ListResult<Menu> selectMenuTrees(Menu menu) {
        List<Module> modules = resourceManager.getModules();

        // 获取已经注册的模块
        String[] toArray = modules.stream().map(item -> item.getMsn()).toArray(String[]::new);

        // 只查询已经注册过的模块的菜单列表
        List<Menu> menuList = this.getBaseDao() // 根据sort排序后的数据
                .selectList(Wrappers.query(menu)
                        .in("msn", toArray)
                        .orderByAsc("sort"));

        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(menuList, 0l);
        return new ListResult<>(menus);
    }

    @Override
    public ListResult<Menu> selectMenuBarTrees(Long aid, boolean isSuper) {
        List<Module> modules = resourceManager.getModules();

        // 获取已经注册的模块
        String[] msnList = modules.stream().map(item -> item.getMsn()).toArray(String[]::new);

        // 只查询已经注册过的模块的菜单列表
        List<Menu> allMenus = this.getBaseDao()
                .selectList(Wrappers.<Menu>query().ne("status"
                        , PermStatus.disabled).in("msn", msnList)
                        .orderByAsc("sort"));

        convertToUrlMap(allMenus);

        if(isSuper) { // 超级管理员拥有所有菜单
            // 转成树结构
            List<Menu> menus = (List<Menu>) TreeUtils.toTrees(allMenus, 0l);
            return new ListResult<>(menus);
        } else { // 普通管理员的菜单列表
            List<String> adminMenuIds = this.getBaseDao().selectAdminMenus(aid);
            return new ListResult<>((List<Menu>) TreeUtils.toAdminMenuTrees(allMenus, adminMenuIds));
        }
    }

    private void convertToUrlMap(List<Menu> allMenus) {
        if(urlMenuMap.size() > 0) return;

        synchronized (allMenus) {
            if(urlMenuMap.size() > 0) return;

            allMenus.forEach(menu -> {
                if(menu.getType() != MenuType.M
                        && StringUtils.hasText(menu.getUrl())) {
                    urlMenuMap.put(menu.getUrl(), menu);
                }
            });
        }

    }

    @Override
    public ListResult selectTreesForNoA(Menu menu) {
        // 去除禁用的状态
        List<Menu> status = this.getBaseDao()
                .selectList(Wrappers.query(menu)
                        .ne("status", PermStatus.disabled)
                        .ne("type", MenuType.A));
        // 转成树结构
        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(status, 0l);
        return new ListResult(menus);
    }

    @Override
    public void createDefaultAuth(Menu menu) {
        if(null == menu) return;
        boolean notBlank = CommonUtils.isNotBlank(menu.getUrl());
        final String urlPrefix = notBlank ? menu.getUrl() : ""
                , permPrefix = notBlank ? permPrefix(menu.getUrl()) : "";

        Stream.of("add", "del", "edit", "view").forEach(item -> {
            String name = "", permType = "";
            Position position = Position.T;
            switch (item) {
                case "add":
                    name = "新增";
                    permType = "Add";
                    position = Position.M;
                    break;
                case "del":
                    name = "删除";
                    permType = "Del";
                    break;
                case "edit":
                    name = "修改";
                    permType = "Edit";
                    break;
                case "view":
                    name = "查询";
                    permType = "View";
                    position = Position.M;
                    break;
            }

            Menu authMenu = Menu.AuthMenu(menu.getId(), urlPrefix + "/" + item, name
                    , menu.getMsn(), permType, permPrefix + ":" + item, position);
            this.insert(authMenu);
        });
    }

    @Override
    public Menu getByUrl(String url) {
        return urlMenuMap.get(url);
    }

    private String permPrefix(String url) {
        if(CommonUtils.isBlank(url)) return "";
        if(url.startsWith("/")) {
            url = url.substring(1, url.length()-1);
        }

        return String.join(":", url.split("/"));
    }
}
