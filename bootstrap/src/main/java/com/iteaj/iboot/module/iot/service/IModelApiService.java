package com.iteaj.iboot.module.iot.service;

import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.framework.IBaseService;

import java.util.List;

/**
 * <p>
 * 物模型接口 服务类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
public interface IModelApiService extends IBaseService<ModelApi> {

    /**
     * 获取指定产品下面的接口列表详情
     * @param productId
     * @return
     */
    ListResult<ModelApi> detailsOfProductId(Long productId);

    /**
     * 通过指定产品和接口代码获取记录
     * @param productId
     * @param code
     * @return
     */
    DetailResult<ModelApi> getByProductIdAndCode(Long productId, String code);

    DetailResult<ModelApi> getByCode(String code);

    Result<ModelApi> detailById(Long id);

    BooleanResult updateAsStatus(Long productId, String code);
}
