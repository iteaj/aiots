package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.AppHomeVo;
import com.iteaj.iboot.module.iot.dto.AppHomeVo.AlarmGradeCount;
import com.iteaj.iboot.module.iot.dto.AppHomeVo.ProductStat;
import com.iteaj.iboot.module.iot.dto.CurrentDeviceDto;
import com.iteaj.iboot.module.iot.dto.FuncStatusProfileDto;
import com.iteaj.iboot.module.iot.dto.GradeCountDto;
import com.iteaj.iboot.module.iot.consts.NameValueDto;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.mapper.WarnRecordMapper;
import com.iteaj.iboot.module.iot.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 移动端首页
 */
@RestController
@RequestMapping("/app/index")
public class AppIndexController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IWarnRecordService warnRecordService;
    private final WarnRecordMapper warnRecordMapper;

    public AppIndexController(IDeviceService deviceService, IProductService productService,
                              IWarnRecordService warnRecordService, WarnRecordMapper warnRecordMapper) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.warnRecordService = warnRecordService;
        this.warnRecordMapper = warnRecordMapper;
    }

    /**
     * 首页统计（设备统计 + 告警统计 + 最新告警 + 产品分布）
     */
    @GetMapping("/statistics")
    public Result<AppHomeVo> statistics() {
        AppHomeVo vo = new AppHomeVo();

        // 1. 设备统计
        CurrentDeviceDto deviceDto = deviceService.countCurrentDevice();
        vo.setDeviceTotal(deviceDto.getToday())
                .setDeviceOnline(deviceDto.getOnline())
                .setDeviceOffline(deviceDto.getToday() - deviceDto.getOnline());

        // 2. 产品数量
        FuncStatusProfileDto profileDto = productService.countStatusProfile();
        vo.setProductTotal(profileDto.getTotalNum());

        // 3. 今日告警设备数（去重）
        Date todayStart = DateUtil.beginOfDay(new Date());
        Date todayEnd = DateUtil.endOfDay(new Date());
        Integer alarmDevices = warnRecordMapper.countDistinctDevice(todayStart, todayEnd);
        vo.setAlarmToday(alarmDevices != null ? alarmDevices : 0);

        // 4. 最新告警（未处理，最近5条）
        Page<WarnRecord> warnPage = new Page<>(1, 5);
        IPage<WarnRecord> warnData = warnRecordService.detailPage(warnPage, new WarnRecord().setProcessStatus(0)).getData();
        if (warnData.getTotal() > 0) {
            vo.setLatestWarns(warnData.getRecords());
        }

        // 5. 各产品设备统计
        List<NameValueDto> productDeviceCounts = productService.countOfDevice();
        if (CollectionUtil.isNotEmpty(productDeviceCounts)) {
            List<ProductStat> productStats = productDeviceCounts.stream()
                    .map(item -> new ProductStat()
                            .setName(item.getName())
                            .setValue(item.getValue() instanceof Number ? ((Number) item.getValue()).intValue() : 0))
                    .collect(Collectors.toList());
            vo.setProductStats(productStats);
        }

        // 6. 各告警等级统计（未处理 + 处理中）
        List<GradeCountDto> gradeCounts = warnRecordMapper.countByGrade();
        if (CollectionUtil.isNotEmpty(gradeCounts)) {
            List<AlarmGradeCount> alarmGradeCounts = gradeCounts.stream()
                    .map(item -> new AlarmGradeCount()
                            .setGradeName(item.getGrade() != null ? WarnGrade.valueOf(item.getGrade()).getDesc() : item.getGrade())
                            .setCount(item.getCount()))
                    .collect(Collectors.toList());
            vo.setAlarmGradeCounts(alarmGradeCounts);
        }

        return success(vo);
    }
}
