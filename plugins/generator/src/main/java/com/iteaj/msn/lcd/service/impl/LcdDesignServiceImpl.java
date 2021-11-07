package com.iteaj.msn.lcd.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.msn.lcd.dto.TableOptionsDto;
import com.iteaj.msn.lcd.entity.LcdDesign;
import com.iteaj.msn.lcd.mapper.LcdDesignMapper;
import com.iteaj.msn.lcd.service.ILcdDesignService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.util.CommonUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        final DetailResult<LcdDesign> one = this.getOne(Wrappers.<LcdDesign>lambdaQuery()
                .eq(LcdDesign::getTableName, entity.getTableName()));

        one.ofNullable().ifPresent(item -> {
            throw new ServiceException("表已经存在["+entity.getTableName()+"]");
        });

        return super.save(entity);
    }

    @Override
    public List<TableOptionsDto> listForeign(String tableName) {
        return list(Wrappers.<LcdDesign>lambdaQuery().select(LcdDesign::getId
                , LcdDesign::getTableName, LcdDesign::getName)).stream().map(table -> {
                final TableOptionsDto tableOptionsDto = new TableOptionsDto(table.getTableName(), table.getTableName());
                if(tableName != null && tableName.equals(table.getTableName())) {
                    tableOptionsDto.setDisabled(true);
                }
                return tableOptionsDto;
            }).collect(Collectors.toList());
    }

    @Override
    public List<TableOptionsDto> listColumns(String tableName) {
        final LcdDesign lcdDesign = getOne(Wrappers.<LcdDesign>lambdaQuery().select(LcdDesign::getId, LcdDesign::getTableName
                , LcdDesign::getName, LcdDesign::getEdit, LcdDesign::getContainer))
                .ofNullable().orElseThrow(() -> new ServiceException("表不存在[" + tableName + "]"));

        final JsonNode jsonNode = lcdDesign.getContainer().get("meta").get("tabModel");
        List<TableOptionsDto> columns = new ArrayList<>();
        if(jsonNode != null && !jsonNode.isNull()) {
            final String field = jsonNode.get("keyField").asText();
            final String keyType = jsonNode.get("keyType").asText();
            columns.add(new TableOptionsDto(field, keyType, jsonNode));
        }

        lcdDesign.getEdit().get("metas").forEach(meta -> {
            final JsonNode tabModel = meta.get("tabModel");
            final String field = tabModel.get("field").asText();
            columns.add(new TableOptionsDto(field, tabModel.get("fieldType").asText(), tabModel));
        });

        return columns;
    }
}
