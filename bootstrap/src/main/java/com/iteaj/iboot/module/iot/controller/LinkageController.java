package com.iteaj.iboot.module.iot.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.iot.consts.*;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.dto.StatusSwitchDto;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.ConditionalsManager;
import com.iteaj.iboot.module.iot.service.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;

import static com.iteaj.iboot.module.iot.entity.LinkageConditionItem.*;

/**
 * 场景联动管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/linkage")
public class LinkageController extends BaseController {

    private final IDeviceService deviceService;
    private final ILinkageService linkageService;
    private final IProductService productService;
    private final IWarnConfigService warnConfigService;
    private final IEventSourceService eventSourceService;

    public LinkageController(IDeviceService deviceService, ILinkageService linkageService, IProductService productService
            , IWarnConfigService warnConfigService, IEventSourceService eventSourceService) {
        this.deviceService = deviceService;
        this.linkageService = linkageService;
        this.productService = productService;
        this.warnConfigService = warnConfigService;
        this.eventSourceService = eventSourceService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览场景联动功能")
    @CheckPermission({"iot:linkage:view"})
    public Result<IPage<Linkage>> list(Page<Linkage> page, Linkage entity) {
        return this.linkageService.detailPage(page, entity);
    }

    /**
     * 规则详情
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public Result<LinkageDto> detail(Long id) {
        return this.linkageService.detailById(id);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:linkage:edit"})
    public Result<Linkage> getById(Long id) {
        return this.linkageService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新场景联动记录")
    @CheckPermission(value = {"iot:linkage:edit", "iot:linkage:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Linkage entity) {
        return this.linkageService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除场景联动记录")
    @CheckPermission({"iot:linkage:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.linkageService.removeAllByIds(idList);
    }

    /**
     * 删除联动项
     * @param itemIds
     * @return
     */
    @PostMapping("/delItem")
    @Logger("删除场景联动项")
    @CheckPermission({"iot:linkage:del"})
    public Result<Boolean> remoteItem(@RequestBody List<Long> itemIds) {
        return this.linkageService.removeByItemIds(itemIds);
    }

    /**
     * 新增或更新联动项
     * @param item
     * @return 联动项id
     */
    @PostMapping("/saveOrUpdateItem")
    @Logger("删除场景联动项")
    @CheckPermission({"iot:linkage:del"})
    public Result<Long> saveOrUpdateItem(@RequestBody LinkageItem item) {
        BooleanResult result = this.linkageService.saveOrUpdateItem(item);
        return result.of().map(status -> status == true ? success(item.getId()) : fail((Long) null, result.getMessage())).get();
    }

    /**
     * 获取联动项详情
     * @param id
     * @return
     */
    @GetMapping("detailItemById")
    public Result<LinkageItem> detailItemById(Long id) {
        return this.linkageService.detailItemById(id);
    }

    /**
     * 更新场景联动状态
     * @param entity
     * @return
     */
    @PostMapping("/switch")
    @Logger("删除场景联动项")
    @CheckPermission({"iot:linkage:switch"})
    public Result<Boolean> switchStatus(@RequestBody StatusSwitchDto<FuncStatus> entity) {
        return this.linkageService.switchStatus(entity.getId(), entity.getStatus());
    }

    /**
     * 获取事件源下面的设备组
     * @param id 联动id
     * @return
     */
    @GetMapping("listDeviceGroupTree")
    public Result<List<DeviceGroup>> listDeviceGroupTree(Long id) {
        if(id == null) {
            return success(Collections.EMPTY_LIST);
        }

        Linkage linkage = linkageService.getById(id).ifNotPresentThrow("联动项不存在").getData();
        return eventSourceService.listDeviceGroupById(linkage.getEventSourceId());
    }

    /**
     * 获取指定产品和设备组下的设备列表
     * @param productId 产品id
     * @param deviceGroupIds
     * @return
     */
    @GetMapping("listDeviceByProductAndDeviceGroupIds")
    public Result<List<Device>> listDeviceByProductAndDeviceGroupIds(Long productId, Long[] deviceGroupIds) {
        List<Device> data = new ArrayList<>();
        if(deviceGroupIds != null && deviceGroupIds.length == 1 && deviceGroupIds[0] != -1) {
            data = deviceService.list(Wrappers.<Device>lambdaQuery()
                    .eq(Device::getProductId, productId)
                    .eq(Device::getDeviceGroupId, deviceGroupIds[0])).getData();
        }

        data.add(0, new Device()
                .setUid(LinkageConditionItem.DEVICE_ANY[0])
                .setName(LinkageConditionItem.DEVICE_ANY[1]));
        data.add(0, new Device()
                .setUid(LinkageConditionItem.DEVICE_TRIGGER[0])
                .setName(LinkageConditionItem.DEVICE_TRIGGER[1]));
        return success(data);
    }

    /**
     * 操作类型列表
     * @return
     */
    @GetMapping("/operators")
    public Result<List<IVOption>> operators() {
        return success(ConditionalsManager.options());
    }

    /**
     * 可用事件源列表
     * @return
     */
    @GetMapping("/sources")
    public Result<List<IVOption>> sources(Long id) {
        return this.eventSourceService.availableSources(id);
    }

    /**
     * 获取指定联动场景下面的组信息
     * @param id
     * @return
     */
    @GetMapping("getDeviceGroups")
    public Result<List<IVOption>> getDeviceGroups(Long id) {

        return success();
    }

    /**
     * 获取此场景下的产品列表
     * @param id
     * @return
     */
    @GetMapping("listProductByLinkageId")
    public Result<List<Product>> listProductByLinkageId(Long id) {
        if(id == null) {
            return fail("未指定场景id");
        }

        LinkageDto linkageDto = this.linkageService.joinProductById(id)
                .ifNotPresentThrow("场景不存在").getData();
        return this.eventSourceService.listProductById(linkageDto.getEventSourceId(), null);
    }

    /**
     * 获取指定设备组以及事件源下所有产品的设备列表
     * @param linkageId 情景联动
     * @param productId 选中的产品
     * @param deviceGroupIds 联动项id
     * @return
     */
    @GetMapping("listDeviceOfAction")
    public Result<List<IVOption>> listDeviceOfAction(Long linkageId, Long productId, Long[] deviceGroupIds) {
        LinkageDto linkage = this.linkageService.joinEventSourceById(linkageId).ifNotPresentThrow("情景联动不存在").getData();
        // 如果没有指定设备组则默认选择事件源下面的所有组
        if(ArrayUtil.isEmpty(deviceGroupIds)) {
            JSONArray groupIds = linkage.getSourceDeviceGroupIds();
            deviceGroupIds = new Long[groupIds.size()];
            for (int i = 0; i < groupIds.size(); i++) {
                deviceGroupIds[i] = groupIds.getLong(i);
            }
        }

        List<IVOption> options = this.deviceService.list(Wrappers.<Device>lambdaQuery()
                .eq(Device::getProductId, productId)
                .in(Device::getDeviceGroupId, deviceGroupIds)).stream()
                .map(device -> new IVOption(device.getName(), device.getUid()))
                .collect(Collectors.toList());

        options.add(0, new IVOption(LinkageAction.DEVICE_ALL[1], LinkageAction.DEVICE_ALL[0]));
        options.add(0, new IVOption(LinkageAction.DEVICE_TRIGGER[1], LinkageAction.DEVICE_TRIGGER[0]));
        return success(options);
    }

    /**
     * 获取事件列表
     * @param productId 产品id
     * @return
     */
    @GetMapping("listEventByProductId")
    public Result<List<IVOption>> listEventByProductId(Long productId) {
        if(productId == null) {
            return fail("参数校验失败");
        }

        List<IVOption> result = new ArrayList<>();
        result.add(new IVOption("设备状态", ConditionType.Status, "").setSelectable(false)
                .addChildren(new IVOption("设备上线", DEVICE_ONLINE[2], "")
                        .addConfig("type", ConditionType.Status)
                        .addConfig("linkedId", DEVICE_ONLINE[0])
                        .addConfig("linkedName", DEVICE_ONLINE[1])
                        .addConfig("attrType", DeviceStatus.online)
                ).addChildren(new IVOption("设备离线", DEVICE_OFFLINE[2], "")
                        .addConfig("type", ConditionType.Status)
                        .addConfig("linkedId", DEVICE_OFFLINE[0])
                        .addConfig("linkedName", DEVICE_OFFLINE[1])
                        .addConfig("attrType", DeviceStatus.offline)
                ));

        this.productService.joinDetailById(productId).ifNotPresentThrow("产品不存在")
                .ifPresent(productDto -> {
                    IVOption attrOption = new IVOption("模型属性", ConditionType.Model, "物模型").setSelectable(false);
                    if(CollectionUtil.isNotEmpty(productDto.getAttrs())) {
                        productDto.getAttrs().forEach(attr -> {
                            if(attr.getAttrType() == AttrType.R || attr.getAttrType() == AttrType.RW) {
                                attrOption.addChildren(new IVOption(attr.getName(), attr.getField(), "")
                                        .addConfig("type", ConditionType.Model)
                                        .addConfig("attrType", attr.getRealType())
                                        .addConfig("linkedId", attr.getId())
                                        .addConfig("linkedName", attr.getName()));
                            }
                        });
                    }

                    result.add(attrOption);
//                    if(!CollectionUtils.isEmpty(productDto.getEventApis())) {
//
//                        productDto.getEventApis().stream().forEach(item -> {
//                            IVOption children = new IVOption(item.getName(), item.getId(), ProtocolApiType.event.getDesc());
//                            List<ModelApiConfig> upConfig = item.getUpConfig();
//                            if(CollectionUtil.isNotEmpty(upConfig)) {
//                                upConfig.forEach(config -> {
//                                    // 点位控制协议使用接口码
//                                    if(productDto.getCtrlMode() == CtrlMode.POINT) {
//                                        children.addChildren(new IVOption(config.getAttrName(), item.getCode(), "属性")
//                                                .addConfig("type", ConditionType.ModelApi)
//                                                .addConfig("code", item.getCode())
//                                                .addConfig("attrType", config.getAttrType())
//                                                .addConfig("linkedName", item.getName())
//                                                .addConfig("linkedId", item.getId()));
//                                    } else {
//                                        children.addChildren(new IVOption(config.getAttrName(), config.getAttrField(), "属性")
//                                                .addConfig("type", ConditionType.ModelApi)
//                                                .addConfig("code", item.getCode())
//                                                .addConfig("attrType", config.getAttrType())
//                                                .addConfig("linkedName", item.getName())
//                                                .addConfig("linkedId", item.getId()));
//                                    }
//                                });
//                                children.setSelectable(false);
//                            }
//
//                            eventApi.addChildren(children);
//                        });
//                    }
//                    result.add(eventApi);
                });
        return success(result);
    }

    @GetMapping("listActionByProductId")
    public Result<List<IVOption>> listActionsByProductIds(Long productId) {
        if(productId == null) {
            return fail("请指定产品参数");
        }

        List<IVOption> result = new ArrayList<>();
        IVOption func = new IVOption("模型接口", ActionType.model, "物模型").setSelectable(false);
        this.productService.joinDetailById(productId).ifPresent(productDto -> {
            if(!CollectionUtils.isEmpty(productDto.getFuncApis())) {
                productDto.getFuncApis().forEach(item -> {
                    IVOption children = new IVOption(item.getName(), ActionType.model + item.getId().toString(), ProtocolApiType.func.getDesc());
                    List<ModelApiConfig> downConfig = item.getDownConfig();
                    if (CollectionUtil.isNotEmpty(downConfig)) {
                        List<IVOption> ivOptions = item.resolveDownApiConfig(productDto.getAttrs());
                        if(CollectionUtil.isNotEmpty(ivOptions)) { // 有可选的配置参数
                            ivOptions.forEach(option -> option.setExtra("参数")
                                    .addConfig("type", ActionType.model)
                                    .addConfig("linkedId", item.getId())
                                    .addConfig("code", item.getCode())
                                    .addConfig("linkedName", item.getName()));
                            children.setChildren(ivOptions).setSelectable(false);
                        } else {
                            children.addConfig("type", ActionType.model)
                                    .addConfig("linkedId", item.getId())
                                    .addConfig("code", item.getCode())
                                    .addConfig("linkedName", item.getName());
                        }
                    }

                    func.addChildren(children);
                });
            }

            if(!CollectionUtils.isEmpty(productDto.getEventApis())) {
                productDto.getEventApis().forEach(item -> {
                    func.addChildren(new IVOption(item.getName(), ActionType.model + item.getId().toString(), ProtocolApiType.event.getDesc())
                            .addConfig("type", ActionType.model)
                            .addConfig("linkedId", item.getId())
                            .addConfig("linkedName", item.getName())
                            .addConfig("code", item.getCode()));
                });
            }
        });

        IVOption warn = new IVOption("事件告警", ActionType.warn, "告警配置").setSelectable(false);
        warnConfigService.list(Wrappers.<WarnConfig>lambdaQuery().eq(WarnConfig::getProductId, productId))
                .ifPresent(warnConfigs -> {
            warnConfigs.forEach(warnConfig -> {
                warn.addChildren(new IVOption(warnConfig.getName(), (ActionType.warn + warnConfig.getId().toString()), warnConfig.getGrade().getDesc())
                        .addConfig("type", ActionType.warn)
                        .addConfig("linkedName", warnConfig.getName())
                        .addConfig("linkedId", warnConfig.getId()));
            });
        });

        result.add(func);
        result.add(warn);
        return success(result);
    }
}

