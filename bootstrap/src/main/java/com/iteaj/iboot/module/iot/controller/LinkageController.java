package com.iteaj.iboot.module.iot.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.iot.consts.ActionType;
import com.iteaj.iboot.module.iot.consts.ConditionType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.consts.OperatorType;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.service.IEventSourceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.service.IWarnConfigService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.service.ILinkageService;
import com.iteaj.framework.BaseController;

/**
 * 场景联动管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/linkage")
public class LinkageController extends BaseController {

    private final ILinkageService linkageService;
    private final IProductService productService;
    private final IWarnConfigService warnConfigService;
    private final IEventSourceService eventSourceService;

    public LinkageController(ILinkageService linkageService, IProductService productService
            , IWarnConfigService warnConfigService, IEventSourceService eventSourceService) {
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
        return this.linkageService.page(page, entity);
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
     * @return
     */
    @PostMapping("/saveOrUpdateItem")
    @Logger("删除场景联动项")
    @CheckPermission({"iot:linkage:del"})
    public Result<Long> saveOrUpdateItem(@RequestBody LinkageItem item) {
        this.linkageService.saveOrUpdateItem(item);
        return success(item.getId());
    }

    /**
     * 更新场景联动状态
     * @param status
     * @param entity
     * @return
     */
    @PostMapping("/switch/{status}")
    @Logger("删除场景联动项")
    @CheckPermission({"iot:linkage:switch"})
    public Result<Boolean> switchStatus(@PathVariable FuncStatus status, @RequestBody Linkage entity) {
        return success();
    }

    /**
     * 操作类型列表
     * @return
     */
    @GetMapping("/operators")
    public Result<List<IVOption>> operators() {
        return success(OperatorType.options());
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
     * 获取此场景下的产品列表
     * @param id
     * @return
     */
    @GetMapping("listProductByLinkageId")
    public Result<List<Product>> listProductByLinkageId(Long id) {
        if(id == null) {
            return fail("未指定场景id");
        }

        LinkageDto linkageDto = this.linkageService.joinDeviceGroupAndProductById(id)
                .ifNotPresentThrow("场景不存在").getData();
        if(CollectionUtil.isNotEmpty(linkageDto.getProductIds())) {
            return productService.listByIds(linkageDto.getProductIds());
        } else {
            return success(Collections.EMPTY_LIST);
        }
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
        this.productService.joinDetailById(productId).ifNotPresentThrow("产品不存在")
                .ifPresent(productDto -> {
                    IVOption eventApi = new IVOption("事件接口", ConditionType.ModelApi, "物模型").setSelectable(false);
                    if(!CollectionUtils.isEmpty(productDto.getEventApis())) {
                        productDto.getEventApis().stream().forEach(item -> {
                            IVOption children = new IVOption(item.getName(), item.getId(), ProtocolApiType.event.getDesc());
                            List<ModelApiConfig> upConfig = item.getUpConfig();
                            if(CollectionUtil.isNotEmpty(upConfig)) {
                                upConfig.forEach(config -> {
                                    children.addChildren(new IVOption(config.getAttrName(), config.getValue(), "属性")
                                            .addConfig("type", ConditionType.ModelApi)
                                            .addConfig("code", item.getCode())
                                            .addConfig("attrType", config.getAttrType())
                                            .addConfig("linkedName", item.getName())
                                            .addConfig("linkedId", item.getId()));
                                });
                                children.setSelectable(false);
                            }

                            eventApi.addChildren(children);
                        });
                    }
                    result.add(eventApi);
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
                    IVOption children = new IVOption(item.getName(), item.getId(), ProtocolApiType.func.getDesc());
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
                        }
                    }

                    func.addChildren(children);
                });
            }

            if(!CollectionUtils.isEmpty(productDto.getEventApis())) {
                productDto.getEventApis().forEach(item -> {
                    func.addChildren(new IVOption(item.getName(), item.getId(), ProtocolApiType.event.getDesc())
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
                warn.addChildren(new IVOption(warnConfig.getName(), warnConfig.getId(), "告警")
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

