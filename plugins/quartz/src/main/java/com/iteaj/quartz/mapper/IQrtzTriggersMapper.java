package com.iteaj.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.quartz.entity.QrtzTriggers;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
public interface IQrtzTriggersMapper extends BaseMapper<QrtzTriggers> {

    QrtzTriggers getDetail(@Param("schedName") String schedName, @Param("jobName") String jobName);

    IPage<QrtzTriggers> joinJobByPage(Page page, QrtzTriggers entity);
}
