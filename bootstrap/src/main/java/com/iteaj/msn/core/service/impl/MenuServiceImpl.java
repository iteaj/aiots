package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.msn.core.mapper.IMenuDao;
import com.iteaj.msn.core.entity.Menu;
import com.iteaj.msn.core.enums.MenuType;
import com.iteaj.msn.core.service.IMenuService;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.result.BooleanResult;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl extends BaseServiceImpl<IMenuDao, Menu> implements IMenuService, InitializingBean {

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

        if(isSuper) { // 超级管理员拥有所有菜单
            // 转成树结构
            List<Menu> menus = (List<Menu>) TreeUtils.toTrees(allMenus, 0l);
            return new ListResult<>(menus);
        } else { // 普通管理员的菜单列表
            List<String> adminMenuIds = this.getBaseDao().selectAdminMenus(aid);
            return new ListResult<>((List<Menu>) TreeUtils.toAdminMenuTrees(allMenus, adminMenuIds));
        }
    }

    @Override
    public ListResult selectParentTrees() {
        // 去除禁用的状态
        List<Menu> status = this.getBaseDao()
                .selectList(Wrappers.<Menu>query()
                        .ne("type", MenuType.A)
                        .ne("status", PermStatus.disabled));

        // 转成树结构
        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(status, 0l);
        return new ListResult(menus);
    }

    @Override
    public BooleanResult deleteByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            throw new ServiceException("未指定要删除的记录");
        }
        if(idList.size() > 1) {
            throw new ServiceException("菜单不支持批量删除");
        }

        this.selectOne(Wrappers.<Menu>query()
                .eq("pid", idList.stream().findFirst().get()))
                .ofNullable().ifPresent((item) -> {throw new ServiceException("请先删除子菜单");});

        return super.deleteByIds(idList);
    }

    @Override
    public Menu getByUrl(String url) {
        return urlMenuMap.get(url);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Module> modules = resourceManager.getModules();

        // 获取已经注册的模块
        String[] msnList = modules.stream().map(item -> item.getMsn()).toArray(String[]::new);

        // 只查询已经注册过的模块的菜单列表
        List<Menu> allMenus = this.getBaseDao()
                .selectList(Wrappers.<Menu>query().ne("type", MenuType.M));

        allMenus.forEach(menu -> {
            if(StringUtils.hasText(menu.getUrl())) {
                urlMenuMap.put(menu.getUrl(), menu);
            }
        });
    }
}
