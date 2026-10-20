package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.framework.IBaseService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 告警记录 服务类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
public interface IWarnRecordService extends IBaseService<WarnRecord> {

    PageResult<?> detailPage(Page page, WarnData entity);

    /**
     * 获取告警记录
     * @param groupId
     * @param size 获取条数
     * @return
     */
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
     * 分页查询告警记录
     * @param page
     * @param uid
     * @return
     */
    PageResult<IPage<WarnRecord>> pageByUid(Page<WarnRecord> page, String uid);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<CountLastWeekDto> countLastWeek(Long deviceGroupId);
}
