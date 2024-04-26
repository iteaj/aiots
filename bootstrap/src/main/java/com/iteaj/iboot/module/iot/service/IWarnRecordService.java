package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.CountLastWeekDto;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.framework.IBaseService;

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

    PageResult<IPage<WarnRecord>> detailPage(Page page, WarnRecord entity);

    /**
     * 获取告警记录
     * @param groupId
     * @param size 获取条数
     * @return
     */
    List<WarnRecord> listByDeviceGroupId(Long groupId, Integer size);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<CountLastWeekDto> countLastWeek(Long deviceGroupId);
}
