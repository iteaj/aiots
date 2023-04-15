package com.iteaj.iboot.msn.core.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.framework.utils.ExcelExportParams;
import com.iteaj.framework.utils.ExcelUtils;
import com.iteaj.iboot.msn.core.dto.AdminDto;
import com.iteaj.iboot.msn.core.dto.PasswordDto;
import com.iteaj.iboot.msn.core.entity.Admin;
import com.iteaj.iboot.msn.core.service.IAdminService;
import com.iteaj.framework.result.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *  后台用户管理功能
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/core/admin")
public class AdminController extends BaseController {

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 查询用户管理列表
     * @param page
     * @param admin
     * @return
     */
    @GetMapping("/view")
    @RequiresPermissions("core:admin:view")
    public Result view(Page page, Admin admin) {
        return this.adminService.page(page, admin);
    }

    /**
     * 新增用户
     * @param admin
     * @return
     */
    @PostMapping("/add")
    @RequiresPermissions("core:admin:add")
    public Result add(@RequestBody AdminDto admin) {
        this.adminService.createAdmin(admin);
        return success();
    }

    /**
     * 获取用户详情
     * @param id
     * @return
     */
    @GetMapping("/edit")
    @RequiresPermissions("core:admin:edit")
    public Result<AdminDto> edit(Long id) {
        return this.adminService.getAdminDetailById(id);
    }

    /**
     * 修改用户
     * @param admin
     * @return
     */
    @PostMapping("/edit")
    @RequiresPermissions("core:admin:edit")
    public Result<Boolean> edit(@RequestBody AdminDto admin) {
        this.adminService.updateAdminAndRole(admin);
        return success(true);
    }

    /**
     * 删除用户
     * @param list
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions("core:admin:del")
    public Result<Boolean> del(@RequestBody List<Long> list) {
        this.adminService.deleteAllJoinByIds(list);
        return success();
    }

    /**
     * 导入用户
     * @param file
     */
    @PostMapping("import")
    public Result<String> excelImport(MultipartFile file) throws IOException {
        List<Admin> admins = ExcelUtils.importExcel(file, Admin.class, new ImportParams());
        this.adminService.saveBatch(admins);

        return success("导入成功");
    }

    /**
     * 导出用户
     * @param page
     * @param admin
     */
    @GetMapping("export")
    public void excelExport(Page<Admin> page, Admin admin, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        this.adminService.page(page, admin).ifPresent((a, b) -> {
            try {
                if(!CollectionUtils.isEmpty(a.getRecords())) {
                    ExcelUtils.exportExcel(a.getRecords(), Admin.class, new ExcelExportParams(), outputStream);
                } else {
                    throw new ServiceException("未指定要导出的数据");
                }
            } catch (IOException e) {
                throw new ServiceException(e.getMessage(), e);
            }
        });
    }

    @PostMapping("/modUserInfo")
    public Result modUserInfo(@RequestBody Admin admin) {
        BaseEntity principal = ShiroUtil.getUser();
        if(principal == null) return fail("未登录");

        admin.setId(principal.getId());
        this.adminService.updateCurrentUserInfo(admin);
        return success("修改成功");
    }

    /**
     * 设置用户密码
     * @return
     */
    @PostMapping("pwd")
    @RequiresPermissions("core:admin:pwd")
    public Result updatePwd(@RequestBody PasswordDto passwordDto) {
        this.adminService.setAdminPassword(passwordDto
                .getId(), passwordDto.getPassword());
        return success("设置成功");
    }
}
