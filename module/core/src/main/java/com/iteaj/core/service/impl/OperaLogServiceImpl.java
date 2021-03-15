package com.iteaj.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iteaj.core.mapper.IOperaLogDao;
import com.iteaj.core.entity.OperaLog;
import com.iteaj.core.service.IOperaLogService;
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
public class OperaLogServiceImpl extends BaseServiceImpl<IOperaLogDao, OperaLog> implements IOperaLogService {

    @Override
    public PageResult<IPage<OperaLog>> selectByPage(IPage<OperaLog> page, OperaLog condition) {
        Object[] section = condition.getSection();
        Object start = section != null && section.length>0 ? section[0] : null;
        Object end = section != null && section.length>1 ? section[1] : null;

        return new PageResult<>(getBaseDao().selectPage(page,
                new QueryWrapper<>(condition)
                        .gt(start!=null, "create_time", start)
                        .lt(end!=null, "create_time", end)));
    }
}
