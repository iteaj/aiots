package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.CollectDataCondition;
import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
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

    IPage<WarnRecord> detailPage(Page page, WarnData entity);

    List<WarnRecord> listByDeviceGroupId(Long groupId, Integer size);

    /**
     * 通过uid和时间范围查询告警（含等级）
     * @param uid 设备uid
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<WarnRecord> listByUidAndTimeRange(String uid, Date startTime, Date endTime);

    /**
     * 分页查询告警记录（按uid）
     * @param page
     * @param uid
     * @return
     */
    IPage<WarnRecord> pageByUid(Page<WarnRecord> page, String uid);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<CountLastWeekDto> countLastWeek(Long deviceGroupId);

    List<BaseEchartsCount> countOfCommonByTime(TimeCountCondition.TimeCountSqlCondition condition);

    List<WarnRecord> listOfCommonByTime(CollectDataCondition.DataSqlCondition item);

    Page pageOfCommonByTime(Page<WarnRecord> page, CollectDataCondition.DataSqlCondition item);
}
