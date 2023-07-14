package com.iteaj.iboot.msn.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iteaj.framework.security.AuthenticationService;
import com.iteaj.iboot.msn.core.dto.AdminDto;
import com.iteaj.iboot.msn.core.entity.Admin;
import com.iteaj.iboot.msn.core.mapper.IAdminDao;
import com.iteaj.iboot.msn.core.service.IAdminService;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.DigestUtils;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.DetailResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl extends BaseServiceImpl<IAdminDao, Admin> implements IAdminService, AuthenticationService {

    @Override
    public Admin getByAccount(String username) {
        return getBaseMapper().selectOne(new QueryWrapper<Admin>()
                .eq("account", username));
    }

    /**
     * 创建管理员
     * @param adminDto
     */
    @Override
    @Transactional
    public void createAdmin(AdminDto adminDto) {
        if(null == adminDto) throw new ServiceException("创建失败");
        Admin byAccount = this.getByAccount(adminDto.getAccount());
        if(null != byAccount) throw new ServiceException("此账号已经存在："+adminDto.getAccount());

        if(CommonUtils.isNotBlank(adminDto.getPassword())) {
            adminDto.setPassword(DigestUtils.md5Hex(adminDto.getPassword().getBytes()));
        } else { // 没有设置密码则密码初始为账号
            adminDto.setPassword(DigestUtils.md5Hex(adminDto.getAccount().getBytes()));
        }

        this.getBaseMapper().createAdmin(adminDto);
    }

    @Transactional
    public void updateAdminAndRole(AdminDto adminDto) {
        this.updateById(adminDto.setUpdateTime(new Date()));
        this.getBaseMapper().updateAdminRole(adminDto);
    }

    @Override
    public DetailResult<AdminDto> getAdminDetailById(Long id) {
        return new DetailResult<>(getBaseMapper().getAdminDetailById(id));
    }

    /**
     * 更新用户密码
     * @param id 用户id
     * @param password 新密码
     * @param oldPwd 旧密码
     */
    @Override
    public void updatePwdById(Long id, String password, String oldPwd) {
        if(null == id || CommonUtils.isBlank(password))
            throw new ServiceException("更新密码失败");
        if(CommonUtils.isBlank(oldPwd))
            throw new ServiceException("请输入原密码");

        oldPwd = DigestUtils.md5Hex(oldPwd.getBytes());
        String oldPassword = getBaseMapper().getAdminPassword(id);

        if(!oldPwd.equalsIgnoreCase(oldPassword)) {
            throw new ServiceException("密码不匹配");
        }

        String md5Hex = DigestUtils.md5Hex(password.getBytes());
        getBaseMapper().updatePwdById(id, md5Hex);
    }

    @Override
    public void deleteAllJoinByIds(List<Long> list) {
        if(CommonUtils.isNotEmpty(list)) {
            getBaseMapper().deleteAllJoinByIds(list);
        }
    }

    @Override
    public List<String> selectPermsById(Serializable id) {
        return getBaseMapper().selectPermsById(id);
    }

    @Override
    public void updateCurrentUserInfo(Admin admin) {
        if(CommonUtils.isNotBlank(admin.getPassword())) {
            String md5Hex = DigestUtils.md5Hex(admin.getPassword().getBytes());
            admin.setPassword(md5Hex);
        }

        this.updateById(admin);

        // 更新当前登录的信息
        Admin principal = (Admin) ShiroUtil.getUser();
        principal.setAvatar(admin.getAvatar()).setEmail(admin.getEmail())
                .setPhone(admin.getPhone()).setRemark(admin.getRemark())
                .setName(admin.getName()).setSex(admin.getSex());
    }

    @Override
    public DetailResult<AdminDto> getAdminCenter(Serializable id) {
        return new DetailResult<AdminDto>(getBaseMapper().getAdminCenter(id));
    }

    @Override
    public void setAdminPassword(Long id, String password) {
        if(null == id || CommonUtils.isBlank(password))
            throw new ServiceException("请检查要设置的密码");

        this.getById(id).ofNullable().orElseThrow(()
                -> new ServiceException("用户不存在"));

        String md5Hex = DigestUtils.md5Hex(password.getBytes());
        getBaseMapper().updatePwdById(id, md5Hex);
    }

}
