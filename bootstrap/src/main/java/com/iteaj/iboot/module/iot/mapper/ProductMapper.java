package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.Product;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 设备型号 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface ProductMapper extends BaseMapper<Product> {

    IPage<ProductDto> pageOfDetail(Page<Product> page, Product entity);

    void removeModel(Long productId);

    ProductDto detailById(Long id);

    boolean hasBind(Collection<? extends Serializable> list);

    List<ProductDto> listOfDetail(Product entity);

    ProductDto joinDetailById(Long productId);

    /**
     * 获取点位协议的产品列表
     * @return
     */
    List<Product> listByPoint();

    List<ProductDto> listJoinDetailByIds(List<Long> productIds);

    /**
     * 是否被启用的事件源绑定
     * @param id
     * @return
     */
    boolean hasBindEnabledEventSource(Long id);

    List<Device> listDeviceByProductId(Long id);

    List<ProductDto> listCtrlStatusModelAttrs(List<Long> productIds);

    /**
     * 获取指定产品下面的状态控制api
     * @param productId
     * @return
     */
    ProductDto getCtrlStatusModelApi(Long productId);

    /**
     * 获取指定协议下面的模型接口
     * @param protocolCode
     * @return
     */
    List<ProductDto> listByProtocolCode(String protocolCode);
}
