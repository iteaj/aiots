package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.module.iot.entity.ModelAttr;

/**
 * <p>
 * 物模型属性 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
public interface ModelAttrMapper extends BaseMapper<ModelAttr> {

    /**
     * 获取物模型属性详情(包含属性字典)
     * @param id
     * @return
     */
    ModelAttr detailById(Long id);
}
