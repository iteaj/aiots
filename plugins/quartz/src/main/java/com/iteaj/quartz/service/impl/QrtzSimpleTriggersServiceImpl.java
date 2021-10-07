package com.iteaj.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iteaj.quartz.entity.QrtzSimpleTriggers;
import com.iteaj.quartz.mapper.IQrtzSimpleTriggersMapper;
import com.iteaj.quartz.service.IQrtzSimpleTriggersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@Service("qrtzSimpleTriggersServiceImpl")
public class QrtzSimpleTriggersServiceImpl extends ServiceImpl<IQrtzSimpleTriggersMapper, QrtzSimpleTriggers> implements IQrtzSimpleTriggersService {

}
