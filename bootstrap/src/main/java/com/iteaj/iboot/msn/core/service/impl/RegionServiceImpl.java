package com.iteaj.iboot.msn.core.service.impl;

import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.msn.core.entity.Region;
import com.iteaj.iboot.msn.core.mapper.IRegionDao;
import com.iteaj.iboot.msn.core.service.IRegionService;
import org.springframework.stereotype.Service;

@Service("regionServiceImpl")
public class RegionServiceImpl extends BaseServiceImpl<IRegionDao, Region> implements IRegionService {

}
