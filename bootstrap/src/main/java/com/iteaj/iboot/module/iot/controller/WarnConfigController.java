package com.iteaj.iboot.module.iot.controller;

import java.util.Collections;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.iot.IotCoreApiService;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.ProductMeta;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.service.IWarnConfigService;
import com.iteaj.framework.BaseController;

/**
 * 告警配置管理
 * @author iteaj
 * @since 2024-02-29
 */
@RestController
@RequestMapping("/iot/warnConfig")
public class WarnConfigController extends BaseController {

    private final IModelAttrService modelAttrService;
    private final IWarnConfigService warnConfigService;
    public WarnConfigController(IModelAttrService modelAttrService, IWarnConfigService warnConfigService) {
        this.modelAttrService = modelAttrService;
        this.warnConfigService = warnConfigService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览告警配置功能")
    @CheckPermission({"iot:warnConfig:view"})
    public Result<IPage<WarnConfig>> list(Page<WarnConfig> page, WarnConfig entity) {
        return this.warnConfigService.pageDetail(page, entity);
    }

    /**
     * 列表查询
     * @param entity 搜索条件
     */
    @GetMapping("/list")
    @Logger("浏览告警配置功能")
    public Result<List<WarnConfig>> list(WarnConfig entity) {
        return this.warnConfigService.list(entity);
    }


    /**
     * 列表查询
     * @param modelAttrId 模型属性id
     */
    @GetMapping("/listByModelAttrId")
    @Logger("根据模型属性id查询告警配置功能")
    public Result<List<WarnConfig>> listByModelAttrId(Long modelAttrId) {
        if(modelAttrId == null) {
            return Result.success(Collections.emptyList(), "");
        }

        return this.warnConfigService.list(Wrappers.<WarnConfig>lambdaQuery().eq(WarnConfig::getModelAttrId, modelAttrId));
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:warnConfig:edit"})
    public Result<WarnConfig> getById(Long id) {
        return this.warnConfigService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新告警配置记录")
    @CheckPermission(value = {"iot:warnConfig:edit", "iot:warnConfig:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody WarnConfig entity) {
        return this.warnConfigService.saveOrUpdate(entity);
    }

    /**
     * 新增或者更新记录
     * @param entity 告警配置
     */
    @PostMapping("/saveOrUpdateFormProduct")
    @Logger("从产品模型新增或者更新告警配置记录")
    public Result<Boolean> saveOrUpdateFormProduct(@RequestBody WarnConfig entity) {
        modelAttrService.getById(entity.getModelAttrId()).ifNotPresentThrow("模型属性不存在")
                .ifPresent(modelAttr -> {
                    entity.setName(modelAttr.getName() + entity.getIndicator() + "[" + entity.getGrade().getDesc() +"]配置");
                    if(entity.getProductId() == null) {
                        entity.setProductId(modelAttr.getProductId());
                    }
                });

        return this.warnConfigService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除告警配置记录")
    @CheckPermission({"iot:warnConfig:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.warnConfigService.removeByIds(idList);
    }

    /**
     * 获取告警等级列表
     * @return
     */
    @GetMapping("grades")
    public Result<List<IVOption>> grades() {
        return success(WarnGrade.options());
    }

    /**
     * 移除状态的告警等级列表
     */
    @GetMapping("gradesRmStatus")
    public Result<List<IVOption>> gradesRmStatus() {
        return success(WarnGrade.optionsRmStatus());
    }
}

