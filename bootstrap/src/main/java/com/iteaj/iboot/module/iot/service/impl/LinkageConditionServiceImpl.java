package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.module.iot.entity.LinkageCondition;
import com.iteaj.iboot.module.iot.entity.LinkageConditionItem;
import com.iteaj.iboot.module.iot.mapper.LinkageConditionMapper;
import com.iteaj.iboot.module.iot.service.ILinkageConditionItemService;
import com.iteaj.iboot.module.iot.service.ILinkageConditionService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 联动条件 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Service
public class LinkageConditionServiceImpl extends BaseServiceImpl<LinkageConditionMapper, LinkageCondition> implements ILinkageConditionService {

    private final ILinkageConditionItemService linkageConditionItemService;

    public LinkageConditionServiceImpl(ILinkageConditionItemService linkageConditionItemService) {
        this.linkageConditionItemService = linkageConditionItemService;
    }

    @Override
    @Transactional
    public BooleanResult save(LinkageCondition entity) {
        return super.save(entity).ifPresentAndTrue(value -> {
            if(CollectionUtil.isNotEmpty(entity.getItems())) {
                for (int i = 0; i < entity.getItems().size(); i++) {
                    LinkageConditionItem linkageConditionItem = entity.getItems().get(i);
                    linkageConditionItem.setLinkageConditionId(entity.getId());
                    if(linkageConditionItem.getSort() == null) {
                        linkageConditionItem.setSort(i * 3 + 3);
                    }
                }

                linkageConditionItemService.saveBatch(entity.getItems());
            }
        });
    }
}
