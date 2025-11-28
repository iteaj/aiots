package com.iteaj.iboot.plugin.iotview.service.impl;

import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.plugin.iotview.entity.Resource;
import com.iteaj.iboot.plugin.iotview.entity.ResourceType;
import com.iteaj.iboot.plugin.iotview.mapper.ResourceMapper;
import com.iteaj.iboot.plugin.iotview.service.IResourceService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 大屏资源 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2025-07-17
 */
@Service
public class ResourceServiceImpl extends BaseServiceImpl<ResourceMapper, Resource> implements IResourceService {

    @Override
    public ListResult<ResourceType> listTypes() {
        return new ListResult<>(getBaseMapper().listTypes());
    }

    @Override
    public ListResult<ResourceType> details(String type) {
        return new ListResult<>(getBaseMapper().details(type));
    }
}
