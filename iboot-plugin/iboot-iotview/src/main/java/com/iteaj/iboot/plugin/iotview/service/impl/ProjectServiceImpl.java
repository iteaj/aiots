package com.iteaj.iboot.plugin.iotview.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.plugin.iotview.entity.Project;
import com.iteaj.iboot.plugin.iotview.mapper.ProjectMapper;
import com.iteaj.iboot.plugin.iotview.service.IProjectService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 大屏项目 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2025-06-04
 */
@Service
public class ProjectServiceImpl extends BaseServiceImpl<ProjectMapper, Project> implements IProjectService {

    @Override
    public BooleanResult save(Project entity) {
        entity.setState(-1);
        this.getOne(Wrappers.<Project>lambdaQuery().eq(Project::getProjectName, entity.getProjectName()))
                .ifPresentThrow("项目名称已经存在");
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Project entity) {
        this.getOne(Wrappers.<Project>lambdaQuery()
                .eq(Project::getId, entity.getId())
                .eq(Project::getProjectName, entity.getProjectName()))
                .ifPresentThrow("项目名称已经存在");
        return super.updateById(entity);
    }
}
