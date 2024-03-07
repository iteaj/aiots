package com.iteaj.iboot.module.iot.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.consts.ConditionType;
import com.iteaj.iboot.module.iot.consts.OperatorType;
import com.iteaj.iboot.module.iot.dto.LinkageDto;
import com.iteaj.iboot.module.iot.entity.ModelApiConfig;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.Linkage;
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

    public LinkageController(ILinkageService linkageService, IProductService productService) {
        this.linkageService = linkageService;
        this.productService = productService;
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
        return this.linkageService.removeByIds(idList);
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
     * @param productId
     * @return
     */
    @GetMapping("listEventByProductId")
    public Result<List<IVOption>> listEventByProductId(Long productId) {
        List<IVOption> result = new ArrayList<>();
        this.productService.joinDetailById(productId)
                .ifNotPresentThrow("产品不存在")
                .ifPresent(productDto -> {
                    IVOption eventApi = new IVOption("事件接口", ConditionType.ModelApi).setSelectable(false);
                    if(!CollectionUtils.isEmpty(productDto.getEventApis())) {
                        productDto.getEventApis().forEach(item -> {
                            IVOption children = new IVOption(item.getName(), item.getId(), item.getCode());
                            List<ModelApiConfig> upConfig = item.getUpConfig();
                            if(CollectionUtil.isNotEmpty(upConfig)) {
                                upConfig.forEach(config -> {
                                    children.addChildren(new IVOption(config.getAttrName(), config.getValue()));
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
}

