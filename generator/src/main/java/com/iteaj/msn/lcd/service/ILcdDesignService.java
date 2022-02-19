package com.iteaj.msn.lcd.service;

import com.iteaj.msn.lcd.dto.TableOptionsDto;
import com.iteaj.msn.lcd.entity.LcdDesign;
import com.iteaj.framework.IBaseService;

import java.util.List;

/**
 * <p>
 * 低代码功能设计 服务类
 * </p>
 *
 * @author iteaj
 * @since 2021-10-22
 */
public interface ILcdDesignService extends IBaseService<LcdDesign> {

    List<TableOptionsDto> listForeign(Long id);

    List<TableOptionsDto> listColumns(String tableName);
}
