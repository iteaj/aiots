package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.IotCoreApiService;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.iboot.module.iot.dto.CollectDataVo;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.entity.ModelAttr;
import com.iteaj.iboot.module.iot.service.ICollectDataService;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 移动端采集数据
 */
@RestController
@RequestMapping("/app/collectData")
public class AppCollectDataController extends BaseController {

    private final ICollectDataService collectDataService;
    private final IDeviceService deviceService;
    private final IModelAttrService modelAttrService;

    public AppCollectDataController(ICollectDataService collectDataService, IDeviceService deviceService, IModelAttrService modelAttrService) {
        this.collectDataService = collectDataService;
        this.deviceService = deviceService;
        this.modelAttrService = modelAttrService;
    }

    /**
     * 历史采集数据分页
     */
    @GetMapping("/page")
    public Result<IPage<CollectDataVo>> page(Page<CollectData> page, @RequestParam String uid) {
        page.addOrder(OrderItem.desc("collect_time"));
        IPage<CollectData> dataPage = collectDataService.lambdaQuery()
                .eq(CollectData::getUid, uid)
                .page(page);

        // 获取设备产品编码
        RealtimeDeviceStatus deviceStatus = IotCoreApiService.getDeviceStatus(uid);
        if(deviceStatus == null)
            return fail("设备不存在");

        // 查询物模型属性构建 field -> unit 映射
        Map<Serializable, ModelAttrMeta> attrMetas = modelAttrService.getAttrMetas(deviceStatus.getProductCode());

        // 转换为 VO 并填充单位
        IPage<CollectDataVo> voPage = dataPage.convert(item -> {
            ModelAttrMeta attrMeta = attrMetas.get(item.getFieldId());
            CollectDataVo vo = new CollectDataVo();
            vo.setField(item.getField());
            vo.setFieldName(item.getFieldName());
            vo.setValue(item.getValue());
            vo.setUnit(attrMeta != null ? attrMeta.getUnit() : "");
            vo.setCollectTime(item.getCollectTime());
            return vo;
        });

        return success(voPage);
    }
}
