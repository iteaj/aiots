package com.iteaj.iboot.module.iot.mapper;

import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 物模型接口 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
public interface ModelApiMapper extends BaseMapper<ModelApi> {

    ModelApi detailById(Long id);

    int updateAsStatus(Long productId, String code);

    List<ModelApi> detailsOfProductId(Long productId);
}
