package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.CollectDataCondition;
import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.iboot.module.iot.dto.GradeCountDto;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
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

    /**
     * 统计指定时间段内产生告警的设备数（去重）
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    Integer countDistinctDevice(@Param("start") Date start, @Param("end") Date end);

    /**
     * 按告警等级统计未处理和处理中的告警数
     * @return
     */
    List<GradeCountDto> countByGrade();

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
