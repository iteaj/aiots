package com.iteaj.msn.core.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.framework.spi.admin.event.OnlineStatus;
import com.iteaj.msn.core.entity.OnlineUser;
import com.iteaj.msn.core.service.IOnlineUserService;
import com.iteaj.util.WebUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 在线用户管理
 * </p>
 *
 * @author iteaj
 * @since 2021-09-25
 */
@RestController
@RequestMapping("/core/onlineUser")
public class OnlineUserController extends BaseController {

    private final IOnlineUserService onlineUserService;

    public OnlineUserController(IOnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity
    */
    @GetMapping("/view")
    @RequiresPermissions(value = {"core:onlineUser:view"})
    public Result<IPage<OnlineUser>> list(Page<OnlineUser> page, OnlineUser entity) {
        page.addOrder(OrderItem.asc("status"), OrderItem.desc("login_time"));
        return this.onlineUserService.page(page, entity);
    }

    /**
    * 删除在线记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions(value = {"core:onlineUser:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        if(idList.size() == 1) {
            this.onlineUserService.getById(idList.get(0)).ofNullable().ifPresent(item -> {
                if(item.getStatus() == OnlineStatus.Online
                        && ShiroUtil.existsSession(item.getSessionId())) {
                    throw new IllegalStateException("此设备在线");
                }
            });
        } else {
            this.onlineUserService.listByIds(idList).forEach(item -> {
                if(item.getStatus() == OnlineStatus.Online) {
                    idList.remove(item.getId());
                }
            });
        }

        return this.onlineUserService.removeByIds(idList);
    }

    /**
     * 剔除用户下线
     * @param entity
     * @return
     */
    @PostMapping("offline")
    @RequiresPermissions(value = {"core:onlineUser:offline"})
    public Result<Boolean> offline(@RequestBody OnlineUser entity, HttpSession session) {
        if(entity.getStatus() == OnlineStatus.Offline) {
            return fail("此用户不在线");
        }

        final String sessionId = session.getId();
        if(sessionId.equals(entity.getSessionId())) {
            return fail("不允许下线自己");
        }

        ShiroUtil.offline(entity.getSessionId()); // 剔除用户下线
        return success("剔除用户成功");
    }
}

