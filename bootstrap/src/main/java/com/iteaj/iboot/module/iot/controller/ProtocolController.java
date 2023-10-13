package com.iteaj.iboot.module.iot.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.file.UploadResult;
import com.iteaj.framework.spi.file.UploadService;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.iboot.module.iot.dto.ProtocolDto;
import com.iteaj.iboot.module.iot.entity.ProtocolApi;
import com.iteaj.iboot.module.iot.entity.ProtocolApiConfig;
import com.iteaj.iboot.module.iot.entity.ProtocolAttr;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.Protocol;
import com.iteaj.iboot.module.iot.service.IProtocolService;
import com.iteaj.framework.BaseController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 报文协议管理
 * @author iteaj
 * @since 2023-09-12
 */
@RestController
@RequestMapping("/iot/protocol")
public class ProtocolController extends BaseController {

    private final UploadService uploadService;
    private final IProtocolService protocolService;

    public ProtocolController(UploadService uploadService, IProtocolService protocolService) {
        this.uploadService = uploadService;
        this.protocolService = protocolService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览报文协议功能")
    @CheckPermission({"iot:protocol:view"})
    public Result<IPage<ProtocolDto>> list(Page<Protocol> page, ProtocolDto entity) {
        return this.protocolService.pageDetail(page, entity);
    }

    /**
     * 获取协议列表
     * @return
     */
    @GetMapping("/list")
    public Result<List<Protocol>> list(Protocol entity) {
        return this.protocolService.list(entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:protocol:edit"})
    public Result<Protocol> getById(Long id) {
        return this.protocolService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新报文协议记录")
    @CheckPermission(value = {"iot:protocol:edit", "iot:protocol:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Protocol entity) {
        entity.setUpdateTime(new Date());
        return this.protocolService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除报文协议记录")
    @CheckPermission({"iot:protocol:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.protocolService.removeByIds(idList);
    }

    /**
     * 导入Jar协议
     * @param file
     * @return
     */
    @Logger("导入Jar协议")
    @PostMapping("loadProtocol")
    public Result<String> loadProtocol(MultipartFile file) throws IOException {
        UploadResult protocol = uploadService.upload(file.getInputStream()
                , file.getOriginalFilename(), file.getOriginalFilename(), "protocol");
        ProtocolSupplierManager.load(protocol.getFile());
        return success(protocol.getUrl(), "加载成功");
    }

    /**
     * 获取协议详情
     * @param id
     * @return
     */
    @GetMapping("detail")
    public Result<ProtocolDto> detail(Long id) {
        return this.protocolService.getByDetail(id).ifPresent(item -> {
            if(item.getImplMode() != ProtocolImplMode.Custom) {
                DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(item.getCode());
                if(supplier != null) {
                    ProtocolModel protocol = supplier.getProtocol();
                    // 协议接口
                    protocol.getApis().values()
                            .stream().forEach(api -> {
                                // 协议接口
                                ProtocolApi protocolApi = new ProtocolApi()
                                    .setProtocolCode(protocol.getCode())
                                    .setCode(api.getCode())
                                    .setName(api.getName())
                                    .setType(api.getType())
                                    .setRemark(api.getRemark());

                                // 协议接口上行配置
                                List<ProtocolApiConfig> upConfig = api.getUpConfig()
                                    .values().stream().map(config -> {
                                        ProtocolModelAttr attr = protocol.getAttrs().get(config.getProtocolModelAttrField());
                                        // 将协议模型的api配置转换成协议api配置
                                        return convertProtocolApiConfig(protocolApi, config, attr);
                                    }).collect(Collectors.toList());

                                // 协议接口下行配置
                                List<ProtocolApiConfig> downConfig = api.getDownConfig()
                                    .values().stream().map(config -> {
                                        ProtocolModelAttr attr = protocol.getAttrs().get(config.getProtocolModelAttrField());
                                        // 将协议模型的api配置转换成协议api配置
                                        return convertProtocolApiConfig(protocolApi, config, attr);
                                    }).collect(Collectors.toList());

                                item.addApis(protocolApi.setUpConfigs(upConfig).setDownConfigs(downConfig));
                            });

                    // 协议属性
                    List<ProtocolAttr> attrs = protocol.getAttrs().values().stream()
                        .map(attr -> new ProtocolAttr()
                        .setProtocolCode(protocol.getCode())
                        .setName(attr.getName())
                        .setField(attr.getField())
                        .setRemark(attr.getRemark())
                        .setType(attr.getType()))
                        .collect(Collectors.toList());
                    item.setAttrs(attrs);
                }
            }
        });
    }

    private ProtocolApiConfig convertProtocolApiConfig(ProtocolApi protocolApi, ProtocolModelApiConfig config, ProtocolModelAttr attr) {
        return new ProtocolApiConfig()
                .setSort(config.getSort())
                .setLength(config.getLength())
                .setRemark(config.getRemark())
                .setPosition(config.getPosition())
                .setProtocolApiCode(protocolApi.getCode())
                .setAttrName(attr != null ? attr.getName() : null)
                .setAttrType(attr != null ? attr.getType() : null)
                .setAttrRemark(attr != null ? attr.getRemark() : null)
                .setProtocolAttrField(config.getProtocolModelAttrField());
    }

    /**
     * 协议实现方式列表
     * @return
     */
    @GetMapping("implModes")
    public Result<List<IVOption>> implModes() {
        return success(ProtocolImplMode.options());
    }

    /**
     * 操控方式列表
     * @return
     */
    @GetMapping("ctrlModes")
    public Result<List<IVOption>> ctrlModes() {
        return success(CtrlMode.options());
    }
}

