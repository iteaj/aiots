package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.mapper.IOrganizeDao;
import com.iteaj.msn.core.entity.Organize;
import com.iteaj.msn.core.service.IAdminService;
import com.iteaj.msn.core.service.IOrganizeService;
import com.iteaj.framework.utils.TreeUtils;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * create time: 2019/11/26
 *
 * @author iteaj
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class OrganizeServiceImpl extends BaseServiceImpl<IOrganizeDao, Organize> implements IOrganizeService {

    private final IAdminService adminService;

    @Override
    public BooleanResult save(Organize entity) {
        final BooleanResult booleanResult = super.save(entity);
        if(entity.getPid() == 0) {
            entity.setLevel(1);
            entity.setPath(entity.getId().toString());
        } else if(entity.getPid() > 0) {
            DetailResult<Organize> parent = getById(entity.getPid());
            parent.ofNullable().orElseThrow(() -> new ServiceException("父部门不存在"));

            entity.setLevel(parent.getData().getLevel() + 1);
            entity.setPath(parent.getData().getPath()+","+entity.getId());
            updateById(entity); // 更新最新路径path
        }

        return booleanResult;
    }

    @Override
    public BooleanResult updateById(Organize entity) {
        if(entity.getPid() > 0) {
            DetailResult<Organize> parent = getById(entity.getPid());
            parent.ofNullable().orElseThrow(() -> new ServiceException("父部门不存在"));

            entity.setLevel(parent.getData().getLevel() + 1);
            entity.setPath(parent.getData().getPath()+","+entity.getId());

            if(parent.getData().getPid() == entity.getId()) {
                throw new ServiceException("父部门不能是自己");
            }
        }

        return super.updateById(entity);
    }

    @Override
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(idList.size() == 0) {
            throw new ServiceException("未选择要删除的数据");
        }
        if(idList.size() > 1) {
            throw new ServiceException("不支持批量删除");
        }
        final Serializable id = idList.stream().findFirst().get();

        // 如果存在子部门, 则要先删除子部门
        list(Wrappers.<Organize>query().eq("pid", id))
                .ofNullable().ifPresent(item -> {
                    if(!item.isEmpty()) {
                        throw new ServiceException("请先删除子部门");
                    }
                });

        // 此部门已经在使用
        adminService.getOne(Wrappers.<Admin>query().eq("org_id", id))
                .ofNullable().ifPresent(item -> {
            throw new ServiceException("此部门已在使用");
        });

        return super.removeByIds(idList);
    }

    @Override
    public ListResult<Organize> selectTrees(Organize organize) {
        ListResult<Organize> selectList = this.list(organize);
        return new ListResult((List<Organize>) TreeUtils.toTrees(selectList.getData(), 0l));
    }
}
