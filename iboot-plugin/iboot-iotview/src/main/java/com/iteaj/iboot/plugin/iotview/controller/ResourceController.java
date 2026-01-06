package com.iteaj.iboot.plugin.iotview.controller;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.file.UploadResult;
import com.iteaj.framework.spi.file.UploadService;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import com.iteaj.iboot.plugin.iotview.entity.Resource;
import com.iteaj.iboot.plugin.iotview.entity.ResourceType;
import com.iteaj.iboot.plugin.iotview.service.IResourceService;
import com.iteaj.iot.utils.ByteUtil;
import com.iteaj.iot.utils.UniqueIdGen;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 大屏资源管理
 * @author iteaj
 * @since 2025-07-17
 */
@RestController
@RequestMapping("/isv/resource")
public class ResourceController extends BaseController {

    private final UploadService uploadService;
    private final IResourceService resourceService;

    public ResourceController(UploadService uploadService, IResourceService resourceService) {
        this.uploadService = uploadService;
        this.resourceService = resourceService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览大屏资源功能")
    public Result<IPage<Resource>> list(Page<Resource> page, Resource entity) {
        return this.resourceService.page(page.addOrder(OrderItem.desc("create_time")), entity);
    }

    /**
     * 大屏素材类型
     * @return
     */
    @GetMapping("/types")
    public Result<List<ResourceType>> types() {
        return this.resourceService.listTypes();
    }


    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"isv:resource:edit"})
    public Result<Resource> getById(Long id) {
        return this.resourceService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新大屏资源记录")
    @CheckPermission(value = {"isv:resource:edit", "isv:resource:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Resource entity) {
        return this.resourceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除大屏资源记录")
    @CheckPermission({"isv:resource:del"})
    public Result<Boolean> remove(@RequestBody List<String> idList) {
        return this.resourceService.removeByIds(idList);
    }

    /**
     * 上传素材图片
     *
     * @param type (背景: bg, 预览图: preview)
     * @param files
     * @return fileurl
     */
    @PostMapping("upload")
    public IotViewResult upload(String type, MultipartFile[] files) {
        if(files.length == 0) {
            return IotViewResult.Fail("未选择文件");
        }
        if(StrUtil.isBlank(type)) {
            return IotViewResult.Fail("未指定资源类型");
        }
        try {
            for (MultipartFile file : files) {
                String name = Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
                final UploadResult isv = uploadService.upload(file.getInputStream(), file.getOriginalFilename(), name, "/isv/resource");
                Resource resource = new Resource().setType(type).setUrl(isv.getUrl()).setName(name).setCreateTime(new Date());
                this.resourceService.save(resource);
            }
            return IotViewResult.Success("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

