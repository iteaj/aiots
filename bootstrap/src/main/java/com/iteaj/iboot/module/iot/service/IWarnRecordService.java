package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.framework.IBaseService;

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
}
