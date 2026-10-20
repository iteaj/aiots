package com.iteaj.iboot.plugin.iotview.service;

import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.plugin.iotview.entity.Resource;
import com.iteaj.framework.IBaseService;
import com.iteaj.iboot.plugin.iotview.entity.ResourceType;

/**
 * <p>
 * 大屏资源 服务类
 * </p>
 *
 * @author iteaj
 * @since 2025-07-17
 */
public interface IResourceService extends IBaseService<Resource> {

    ListResult<ResourceType> listTypes();

    ListResult<ResourceType> details(String type);
}
