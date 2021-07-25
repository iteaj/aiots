package com.iteaj.msn.core.controller;

import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.utils.ShiroUtil;
import com.iteaj.msn.core.dto.AdminDto;
import com.iteaj.msn.core.dto.PasswordDto;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.service.IAdminService;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.BaseHttpController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * create time: 2021/6/5
 * 用户管理中心接口
 *
 * 获取校验码接口
 * @see com.iteaj.framework.autoconfigure.CaptchaAutoConfiguration#captcha(HttpSession, HttpServletResponse)
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/center")
public class AdminCenterController extends BaseHttpController {

    @Autowired
    private WebAuthHandler handler;
    @Autowired
    private IAdminService adminService;

    /**
     * 表单登录
     * @return
     */
    @PostMapping("login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        handler.login(request, response);
    }

    /**
     * 注销
     * @param request
     * @param response
     * @return
     */
    @PostMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        handler.logout(request, response);
    }

    /**
     * 用户中心头像
     * @param file
     * @return
     */
    @PostMapping("avatar")
    public Result<String> avatar(MultipartFile file) {

        return success("");
    }

    /**
     * 用户详情
     * @return
     */
    @GetMapping("detail")
    public Result<AdminDto> detail() {
        Serializable id = ShiroUtil.getUser().getId();

        return adminService.getAdminCenter(id);
    }

    /**
     * 修改用户
     * @param admin
     * @return
     */
    @PostMapping("editUser")
    public Result<Boolean> updateUser(@RequestBody Admin admin) {
        return adminService.updateById(admin);
    }

    /**
     * 修改密码
     * @return
     */
    @PostMapping("pwd")
    public Result<Boolean> updatePwd(@RequestBody PasswordDto passwordDto) {
        this.adminService.updatePwdById(passwordDto.getId()
                , passwordDto.getPassword(), passwordDto.getOldPwd());
        return success("修改成功");
    }
}
