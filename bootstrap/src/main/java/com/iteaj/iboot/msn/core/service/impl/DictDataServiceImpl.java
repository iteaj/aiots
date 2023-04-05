package com.iteaj.iboot.msn.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.iboot.common.CacheKeys;
import com.iteaj.iboot.msn.core.entity.DictData;
import com.iteaj.iboot.msn.core.mapper.IDictDataDao;
import com.iteaj.iboot.msn.core.service.IDictDataService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * jdk：1.8
 *
 * @author iteaj
 * create time：2019/6/24
 */
@Service
@CacheConfig(cacheNames = CacheKeys.IBOOT_CACHE_DICT)
public class DictDataServiceImpl extends BaseServiceImpl<IDictDataDao, DictData> implements IDictDataService {

    @Override
    public ListResult<DictData> selectByType(String type) {
        List<DictData> list = getBaseMapper().selectList(Wrappers.<DictData>query().eq("type", type));
        return new ListResult<>(list);
    }

    @Override
    public BooleanResult removeByType(String type) {
        return this.remove(Wrappers.<DictData>lambdaQuery().eq(DictData::getType, type));
    }

    @Override
    @Cacheable(key = "'DICT:LIST'")
    public ListResult<DictData> list() {
        return super.list();
    }

}
