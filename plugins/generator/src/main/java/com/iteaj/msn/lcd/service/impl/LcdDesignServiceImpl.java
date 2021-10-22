package com.iteaj.msn.lcd.service.impl;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.msn.lcd.entity.LcdDesign;
import com.iteaj.msn.lcd.mapper.LcdDesignMapper;
import com.iteaj.msn.lcd.service.ILcdDesignService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.util.CommonUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 低代码功能设计 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2021-10-22
 */
@Service
public class LcdDesignServiceImpl extends BaseServiceImpl<LcdDesignMapper, LcdDesign> implements ILcdDesignService {

    @Override
    public BooleanResult save(LcdDesign entity) {
        if(CommonUtils.isBlank(entity.getName())) {
            throw new ServiceException("功能名必填");
        }
        if(CommonUtils.isBlank(entity.getTableName())) {
            throw new ServiceException("表名必填");
        }
        return super.save(entity);
    }
}
