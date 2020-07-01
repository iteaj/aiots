package com.iteaj.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iteaj.core.dto.AdminDto;
import com.iteaj.core.entity.Admin;
import com.iteaj.core.mapper.IAdminDao;
import com.iteaj.core.service.IAdminService;
import com.iteaj.util.CommonUtils;
import com.iteaj.util.DigestUtils;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.ServiceException;
import com.iteaj.util.module.mvc.result.DetailResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
public class AdminServiceImpl extends BaseServiceImpl<IAdminDao, Admin> implements IAdminService {

    @Override
    public Admin getByAccount(String username) {
        return getBaseDao().selectOne(new QueryWrapper<Admin>()
                .eq("account", username));
    }

    /**
     * 创建管理员
     * @param adminDto
     */
    @Override
    @Transactional
    public void createAdmin(AdminDto adminDto) {
        if(null == adminDto) throw new ServiceException("创建失败, 管理员对象不存在", "创建失败");
        Admin byAccount = this.getByAccount(adminDto.getAccount());
        if(null != byAccount) throw new ServiceException("此账号已经存在："+adminDto.getAccount());

        if(CommonUtils.isNotBlank(adminDto.getPassword())) {
            adminDto.setPassword(DigestUtils.md5Hex(adminDto.getPassword().getBytes()));
        } else { // 没有设置密码则密码初始为账号
            adminDto.setPassword(DigestUtils.md5Hex(adminDto.getAccount().getBytes()));
        }

        this.getBaseDao().createAdmin(adminDto);
    }

    @Transactional
    public void updateAdminAndRole(AdminDto adminDto) {
        this.getBaseDao().updateAdmin(adminDto);
    }

    @Override
    public DetailResult<AdminDto> getAdminDetailById(Long id) {
        return new DetailResult<>(getBaseDao().getAdminDetailById(id));
    }

    /**
     * 更新用户密码
     * @param id
     * @param password
     */
    @Override
    public void updatePwdById(Long id, String password) {
        if(null == id || CommonUtils.isBlank(password))
            throw new ServiceException("更新参数错误", "更新密码失败");

        String md5Hex = DigestUtils.md5Hex(password.getBytes());
        getBaseDao().updatePwdById(id, md5Hex);
    }

    @Override
    public void deleteAllJoinByIds(List<Long> list) {
        if(CommonUtils.isNotEmpty(list)) {
            getBaseDao().deleteAllJoinByIds(list);
        }
    }

    @Override
    public List<String> selectPermsById(Serializable id) {
        return getBaseDao().selectPermsById(id);
    }

}
