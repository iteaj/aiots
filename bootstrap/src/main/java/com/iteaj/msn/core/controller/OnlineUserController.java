package com.iteaj.msn.core.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.msn.core.entity.OnlineUser;
import com.iteaj.msn.core.service.IOnlineUserService;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.result.ListResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    private WebAuthHandler handler;
    @Autowired
    private IOnlineUserService onlineUserService;

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
    public Result del(@RequestBody List<Long> ids, HttpServletRequest request, HttpServletResponse response) {
        ListResult<OnlineUser> result = this.onlineUserService.selectByIds(ids);
        List<OnlineUser> onlineUsers = result.get();
        if(CollectionUtils.isNotEmpty(onlineUsers)) {

            OnlineUser onlineUser = onlineUsers.get(0);

            return success("强退成功: " + onlineUser.getUserNick());
        }

        return fail("此用户不存在");
    }

}

