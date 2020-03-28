package com.iteaj.admin.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.admin.dao.IMenuDao;
import com.iteaj.admin.entity.Menu;
import com.iteaj.admin.enums.MenuType;
import com.iteaj.admin.service.IMenuService;
import com.iteaj.framework.consts.PermStatus;
import com.iteaj.framework.consts.Position;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.result.BooleanResult;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Stream;

@Service
@SofaService(bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
public class MenuServiceImpl extends BaseServiceImpl<IMenuDao, Menu> implements IMenuService {

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
        List<Menu> menuList = this.getBaseDao() // 根据sort排序后的数据
                .selectList(Wrappers.query(menu).orderByAsc("sort"));
        List<Menu> menus = (List<Menu>) TreeUtils.toTrees(menuList, 0l);
        return new ListResult<>(menus);
    }

    @Override
    public ListResult<Menu> selectMenuBarTrees(Long aid, boolean isSuper) {
        List<Menu> allMenus = this.getBaseDao()
                .selectList(Wrappers.<Menu>query().ne("status"
                        , PermStatus.disabled).orderByAsc("sort"));
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

    private String permPrefix(String url) {
        if(CommonUtils.isBlank(url)) return "";
        if(url.startsWith("/")) {
            url = url.substring(1, url.length()-1);
        }

        return String.join(":", url.split("/"));
    }
}
