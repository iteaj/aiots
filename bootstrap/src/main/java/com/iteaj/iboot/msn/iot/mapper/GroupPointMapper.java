package com.iteaj.iboot.msn.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.msn.iot.entity.GroupPoint;

import java.util.List;

public interface GroupPointMapper extends BaseMapper<GroupPoint> {

    void batchSaveGroupPoint(List<GroupPoint> list);
}
