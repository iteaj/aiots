package com.iteaj.msn.core.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.msn.core.auth.AdminAuthToken;
import com.iteaj.msn.core.dto.AdminDto;
import com.iteaj.msn.core.dto.PasswordDto;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.service.IAdminService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * 用户管理中心接口
 * @see com.iteaj.framework.autoconfigure.CaptchaAutoConfiguration#captcha(HttpSession, HttpServletResponse) 获取校验码接口
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/center")
public class LoginController extends BaseController {

    @Autowired
    private IAdminService adminService;

    /**
     * 系统登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody AdminAuthToken token) {
        try {
            SecurityUtils.getSubject().login(token);
        } catch (SecurityException e) {
            return fail(e.getMessage());
        } catch (AuthenticationException e) {
            return fail(e.getMessage());
        } catch (Exception e) {
            return fail("登录失败");
        }

        return success("登录成功");
    }

    /**
     * 系统注销
     * @return
     */
    @PostMapping("logout")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    /**
     * 上传用户头像
     * @param file
     * @return
     */
    @PostMapping("avatar")
    public Result<String> avatar(MultipartFile file) {
        return success("");
    }

    /**
     * 获取用户详情
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
     * 修改用户密码
     * @return
     */
    @PostMapping("pwd")
    public Result<Boolean> updatePwd(@RequestBody PasswordDto passwordDto) {
        this.adminService.updatePwdById(passwordDto.getId()
                , passwordDto.getPassword(), passwordDto.getOldPwd());
        return success("修改成功");
    }
}
