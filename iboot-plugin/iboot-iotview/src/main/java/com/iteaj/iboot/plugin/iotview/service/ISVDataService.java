package com.iteaj.iboot.plugin.iotview.service;

import com.iteaj.framework.spi.iot.view.DataItemOption;

import java.util.List;

public interface ISVDataService {

    List<DataItemOption> listProductOption();

    List<DataItemOption> listModelAttrOption(String productId);
}
