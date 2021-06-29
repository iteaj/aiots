package com.iteaj.msn.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iteaj.msn.core.mapper.IAccessLogDao;
import com.iteaj.msn.core.entity.AccessLog;
import com.iteaj.msn.core.service.IAccessLogService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import com.iteaj.util.module.mvc.result.PageResult;
import org.springframework.stereotype.Service;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
@Service
public class AccessLogServiceImpl extends BaseServiceImpl<IAccessLogDao, AccessLog> implements IAccessLogService {

    @Override
    public PageResult<IPage<AccessLog>> selectByPage(IPage<AccessLog> page, AccessLog condition) {
        Object[] section = condition.getSection();
        Object start = section != null && section.length>0 ? section[0] : null;
        Object end = section != null && section.length>1 ? section[1] : null;

        return new PageResult<>(getBaseDao().selectPage(page,
                new QueryWrapper<>(condition)
                        .gt(start!=null, "create_time", start)
                        .lt(end!=null, "create_time", end)));
    }
}
