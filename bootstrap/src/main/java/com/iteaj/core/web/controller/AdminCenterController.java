package com.iteaj.core.web.controller;

import com.iteaj.core.dto.AdminDto;
import com.iteaj.core.dto.PasswordDto;
import com.iteaj.core.service.IAdminService;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.BaseHttpController;
import com.iteaj.util.module.mvc.result.DetailResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * create time: 2021/6/5
 * 用户管理中心
 * @author iteaj
 * @since 1.0
 */
@Api(tags = "用户管理中心")
@RestController
@RequestMapping("/core/center")
public class AdminCenterController extends BaseHttpController {

    @Autowired
    private IAdminService adminService;

    @ApiOperation("用户详情")
    @RequestMapping("detail")
    public Result<AdminDto> detail() {
        Serializable id = SecurityUtils.getUser().getId();

        return adminService.getAdminCenter(id);
    }

    /**
     * 修改密码
     * @return
     */
    @ResponseBody
    @PostMapping("pwd")
    public Result updatePwd(@RequestBody PasswordDto passwordDto) {
        this.adminService.updatePwdById(passwordDto.getId()
                , passwordDto.getPassword(), passwordDto.getOldPwd());
        return success("修改成功");
    }
}
