package com.iteaj.iboot.plugin.iotview.mapper;

import com.iteaj.iboot.plugin.iotview.entity.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.plugin.iotview.entity.ResourceType;

import java.util.List;

/**
 * <p>
 * 大屏资源 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2025-07-17
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    List<ResourceType> listTypes();

    List<ResourceType> details(String type);
}
