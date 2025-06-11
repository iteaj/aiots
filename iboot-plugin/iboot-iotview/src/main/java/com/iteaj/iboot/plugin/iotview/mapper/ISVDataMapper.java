package com.iteaj.iboot.plugin.iotview.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.iteaj.framework.spi.iot.view.DataItemOption;

import java.util.List;

public interface ISVDataMapper extends Mapper {

    List<DataItemOption> listProductOption();

    List<DataItemOption> listModelAttrOption(String productId);

}
