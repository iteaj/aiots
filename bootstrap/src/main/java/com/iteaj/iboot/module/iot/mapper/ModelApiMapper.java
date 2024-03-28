package com.iteaj.iboot.module.iot.mapper;

import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
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

    CtrlMode getProtocolCtrlModelByProductId(Long productId);

    /**
     * 是否有相同点位地址
     * @param productId
     * @param type
     * @param pointAddress
     * @param value
     * @return
     */
    boolean hasSameAddress(Long productId, ProtocolApiType type, String pointAddress, String value);

    /**
     * 产品状态是否为启用
     * @param productId
     * @return
     */
    boolean isEnabled(Long productId);
}
