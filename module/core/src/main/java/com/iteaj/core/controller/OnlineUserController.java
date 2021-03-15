package com.iteaj.core.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.core.entity.OnlineUser;
import com.iteaj.core.service.IOnlineUserService;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.result.ListResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import com.iteaj.framework.BaseController;

/**
 * <p>
 * 在线用户 控制器
 * </p>
 *
 * @author iteaj
 * @since 2020-06-20
 */
@Controller
@RequestMapping("/core/online")
public class OnlineUserController extends BaseController {

    @Autowired
    private IOnlineUserService onlineUserService;
    @Autowired
    private AuthenticatingService authenticatingService;


    @GetMapping
    @RequiresPermissions("core:online:view")
    public String view() {
        return "onlineUser";
    }

    @ResponseBody
    @GetMapping("/view")
    @RequiresPermissions("core:online:view")
    public Result view(OnlineUser entity, Page page) {
        return this.onlineUserService.selectByPage(page
                , Wrappers.query(entity)
                        .ne("status", OnlineType.Guest)
                        .orderByAsc("status")
                        .orderByDesc("create_time"))
                .build(success());
    }

    @ResponseBody
    @GetMapping("/detail")
    @RequiresPermissions("core:online:detail")
    public Result detail(Long id) {
        return this.onlineUserService.getById(id).build(success());
    }

    /**
     * 强制退出
     * @param ids
     * @return
     */
    @ResponseBody
    @PostMapping("/del")
    @RequiresPermissions("core:online:del")
    public Result del(@RequestBody List<Long> ids) {
        ListResult<OnlineUser> result = this.onlineUserService.selectByIds(ids);
        List<OnlineUser> onlineUsers = result.get();
        if(CollectionUtils.isNotEmpty(onlineUsers)) {

            OnlineUser onlineUser = onlineUsers.get(0);

            // 如果是当前用户不能强退
            if(authenticatingService.equalToCurrent(onlineUser.getSessionId())) {
                return fail("不允许强退当前会话");
            }

            authenticatingService.logout(onlineUser.getSessionId());

            // 强制离线
//            this.onlineUserService.updateBySessionId(new OnlineUser((String) onlineUser
//                    .getSessionId()).setStatus(OnlineType.Off.name()));

            return success("强退成功: " + onlineUser.getUserNick());
        }

        return fail("此用户不存在");
    }

}

