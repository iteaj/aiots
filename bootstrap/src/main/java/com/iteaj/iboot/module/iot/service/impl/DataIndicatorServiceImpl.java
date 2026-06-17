package com.iteaj.iboot.module.iot.service.impl;

import com.iteaj.iboot.module.iot.dto.IndicatorStats;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.mapper.CollectDataMapper;
import com.iteaj.iboot.module.iot.service.IDataIndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据指标服务实现
 */
@Service
public class DataIndicatorServiceImpl implements IDataIndicatorService {

    @Autowired
    private CollectDataMapper collectDataMapper;

    @Override
    public IndicatorStats getStats(String uid, Long fieldId, String start, String end) {
        return collectDataMapper.statsOfField(uid, fieldId, start, end);
    }

    @Override
    public List<CollectData> listData(String uid, Long fieldId, String start, String end) {
        return collectDataMapper.listOfData(uid, fieldId, start, end);
    }

    @Override
    public List<CollectData> associatedPoints(String uid, Long excludeFieldId, String collectTime) {
        return collectDataMapper.associatedPoints(uid, excludeFieldId, collectTime);
    }

    @Override
    public List<CollectData> crossDevicePoints(String productCode, Long fieldId, String collectTime, String excludeUid) {
        return collectDataMapper.crossDevicePoints(productCode, fieldId, collectTime, excludeUid);
    }
}
