package com.iteaj.admin.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.iteaj.admin.dao.IMenuViewDao;
import com.iteaj.admin.entity.MenuView;
import com.iteaj.admin.service.IMenuViewService;
import com.iteaj.util.module.mvc.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
@SofaService(bindings = {@SofaServiceBinding(bindingType = "${bind.type}")})
public class MenuViewServiceImpl extends BaseServiceImpl<IMenuViewDao, MenuView>
        implements IMenuViewService {

}
