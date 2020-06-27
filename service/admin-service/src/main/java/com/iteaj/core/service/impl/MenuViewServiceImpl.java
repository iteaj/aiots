package com.iteaj.core.service.impl;

import com.iteaj.core.mapper.IMenuViewDao;
import com.iteaj.core.entity.MenuView;
import com.iteaj.core.service.IMenuViewService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MenuViewServiceImpl extends BaseServiceImpl<IMenuViewDao, MenuView>
        implements IMenuViewService {

}
