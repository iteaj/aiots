package com.iteaj.iboot.module.iot.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.file.UploadResult;
import com.iteaj.framework.spi.file.UploadService;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.iboot.module.iot.consts.IotConsts;
import com.iteaj.iboot.module.iot.dto.ProtocolDto;
import com.iteaj.iboot.module.iot.entity.ProtocolApi;
import com.iteaj.iboot.module.iot.entity.ProtocolApiConfig;
import com.iteaj.iboot.module.iot.entity.ProtocolAttr;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
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
    public Result<Protocol> loadProtocol(MultipartFile file) throws IOException {
        // 先上传到临时文件
        UploadResult protocol = uploadService.upload(file.getInputStream()
                , file.getOriginalFilename(), file.getOriginalFilename(), ProtocolSupplierManager.PROTOCOL_JAR_TEMP_DIR);

        List<DeviceProtocolSupplier> load = ProtocolSupplierManager.loadFromTempProtocol(protocol.getFileName());
        if(load.size() > 1) {
            protocol.getFile().delete();
            return fail("jar文件只支持一个协议提供");
        } else if(load.size() == 0) {
            // jar包里面没有任何协议
            protocol.getFile().delete();
            return fail("未包含任务协议");
        }
        DeviceProtocolSupplier protocolSupplier = load.get(0);
        ProtocolModel protocolModel = protocolSupplier.getProtocol();
        DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(protocolModel.getCode());
        if(supplier != null) {
            if(supplier.getVersion().equals(protocolSupplier.getVersion())) {
                return fail("已存在相同版本的协议");
            }
        }

        ProtocolDto loadProtocol = this.protocolService.loadProtocol(protocolSupplier);
        loadProtocol.setJarPath(protocol.getFileName());
        Protocol result = new Protocol();
        BeanUtils.copyProperties(loadProtocol, result);
        return success(result, "加载成功");
    }

    /**
     * 获取协议详情
     * @param id
     * @return
     */
    @GetMapping("detail")
    public Result<ProtocolDto> detail(Long id) {
        return this.protocolService.getByDetail(id);
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

    /**
     * 获取协议对应的api列表
     * @param id 协议id
     * @param type 接口类型
     * @return
     */
    @GetMapping("apis")
    public Result<List<ProtocolApi>> apis(Long id, ProtocolApiType type) {
        List<ProtocolApi> protocolApis = this.protocolService.getByDetail(id).ofNullable().map(item -> {
            if (type == ProtocolApiType.func) {
                return item.getFuncApis();
            } else if (type == ProtocolApiType.event) {
                return item.getEventApis();
            } else {
                ArrayList<ProtocolApi> list = new ArrayList<>();
                if(!CollectionUtils.isEmpty(item.getFuncApis())) {
                    list.addAll(item.getFuncApis());
                }

                if(!CollectionUtils.isEmpty(item.getEventApis())) {
                    list.addAll(item.getEventApis());
                }
                return list;
            }
        }).orElseGet(ArrayList::new);
        return success(protocolApis);
    }
}

