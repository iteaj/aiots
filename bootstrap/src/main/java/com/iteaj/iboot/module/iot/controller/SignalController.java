package com.iteaj.iboot.module.iot.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.afterturn.easypoi.handler.impl.ExcelDataHandlerDefaultImpl;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.excel.ExcelImportParams;
import com.iteaj.framework.utils.ExcelUtils;
import com.iteaj.iboot.module.iot.dto.SignalOfModelDto;
import com.iteaj.iboot.module.iot.entity.DeviceModel;
import com.iteaj.iboot.module.iot.entity.Signal;
import com.iteaj.iboot.module.iot.service.IDeviceModelService;
import com.iteaj.iboot.module.iot.service.ISignalService;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 寄存器点位管理
 *
 * @author iteaj
 * @since 2022-07-22
 */
@RestController
@RequestMapping("/iot/signal")
public class SignalController extends BaseController {

    private final ISignalService signalService;
    private final IDeviceModelService deviceModelService;

    public SignalController(ISignalService signalService, IDeviceModelService deviceModelService) {
        this.signalService = signalService;
        this.deviceModelService = deviceModelService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @CheckPermission({"iot:signal:view"})
    public Result<IPage<Signal>> list(Page<Signal> page, Signal entity) {
        return this.signalService.detailByPage(page, entity);
    }

    /**
     * 获取指定型号下面的点位列表
     * @param modelId 型号id
     */
    @GetMapping("/listByModel")
    public Result<List<SignalOfModelDto>> listByModel(Integer modelId) {
        List<SignalOfModelDto> modelDtos = this.signalService.list(new Signal(modelId))
                .stream().map(item -> new SignalOfModelDto().setId(item.getId().toString())
                .setName(item.getName()).setAddress(item.getAddress())
                .setFieldName(item.getFieldName())).collect(Collectors.toList());
        return success(modelDtos);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:signal:edit"})
    public Result<Signal> getById(Long id) {
        return this.signalService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:signal:edit", "iot:signal:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Signal entity) {
        return this.signalService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:signal:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.signalService.removeByIds(idList);
    }

    /**
     * 导入点位
     * @param file
     */
    @Logger("导入点位")
    @PostMapping("import")
    public Result<String> excelImport(MultipartFile file) throws IOException {
        ExcelImportParams excelImportParams = new ExcelImportParams();
        Map<String, Long> longMap = this.deviceModelService.list().stream()
                .collect(Collectors.toMap(DeviceModel::getModel, DeviceModel::getId));
        excelImportParams.setDataHandler(new ExcelDataHandlerDefaultImpl() {
            @Override
            public String[] getNeedHandlerFields() {
                return new String[]{"所属型号"};
            }

            @Override
            public Object importHandler(Object obj, String name, Object value) {
                Long aLong = longMap.get(value);
                return aLong;
            }
        });

        List<Signal> signals = ExcelUtils.importExcel(file, Signal.class, excelImportParams);
        this.signalService.batchImport(signals);
        return success("导入成功");
    }
}

