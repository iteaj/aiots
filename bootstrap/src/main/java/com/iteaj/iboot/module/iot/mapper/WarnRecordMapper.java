package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 告警记录 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
public interface WarnRecordMapper extends BaseMapper<WarnRecord> {

    IPage<WarnRecord> detailPage(Page page, WarnRecord entity);

    List<WarnRecord> listByDeviceGroupId(Long groupId, Integer size);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<CountLastWeekDto> countLastWeek(Long deviceGroupId);
}
