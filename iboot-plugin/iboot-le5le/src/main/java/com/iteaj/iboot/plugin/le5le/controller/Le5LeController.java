package com.iteaj.iboot.plugin.le5le.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.plugin.le5le.entity.Le5LeDatasource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 乐吾乐大屏接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/iot/screen")
public class Le5LeController extends BaseController {

    private final Le5LeService le5LeService;

    @GetMapping("fields")
    public JSONArray fields() {
        JSONArray objects = new JSONArray();
        JSONObject product1 = new JSONObject();
        product1.put("id", "product1");
        product1.put("name", "断路器");
        JSONArray children = new JSONArray();
        product1.put("children", children);
        JSONObject breaker1 = new JSONObject();
        breaker1.put("id", "product1-breaker1");
        breaker1.put("name", "1号设备");
        children.add(breaker1);
        objects.add(product1);
        return objects;
    }

    /**
     * 获取列表
     * @param page
     * @return
     */
    @GetMapping("list")
    public Result<Page> list(Page page, String type) {
        if("2d".equals(type)) {
            return success(le5LeService.list2d(page));
        } else if("3d".equals(type)) {
            return success(le5LeService.list3d(page));
        } else if("dbs".equals(type)) {
            return success(le5LeService.list(page));
        }

        return fail("未知的类型["+type+"]");
    }

    /**
     * 获取大屏详情
     * @param id
     * @return
     */
    @GetMapping("get")
    public Result<JSONObject> get(String id, String type) {
        return success(le5LeService.get(id, type));
    }

    /**
     * 数据同步
     * @param id
     * @param type 类型(2d、3d、dbs)
     * @return
     */
    @GetMapping("sync")
    public Result<Boolean> sync(String id, String type) {
        return le5LeService.sync(id, type);
    }

    /**
     * 离线部署
     * @param file
     * @param entity 安装路径
     * @return
     */
    @GetMapping("offlineInstall")
    public Result<Boolean> offlineInstall(MultipartFile file, Le5LeDatasource entity) {
        le5LeService.offlineInstall(file, entity);
        return success();
    }

    /**
     * 获取数据源列表
     * @param page
     * @param entity
     * @return
     */
    @GetMapping("listDatasource")
    public Result<Page<Le5LeDatasource>> listDatasource(Page page, Le5LeDatasource entity) {
        return success(le5LeService.pageDateSet(page, entity));
    }

    /**
     * 获取数据源列表
     * @param id
     * @return
     */
    @GetMapping("getDatasource")
    public Result<Le5LeDatasource> getDatasource(String id) {
        return success(le5LeService.getDatasource(id));
    }

    /**
     * 增加或者修改数据源
     * @param entity
     * @return
     */
    @PostMapping("saveOrUpdate")
    @CheckPermission(value = {"iot:screen:addDatasource", "iot:screen:editDatasource"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Le5LeDatasource entity) {
        return le5LeService.saveOrUpdate(entity);
    }

    /**
     * 删除数据源
     * @param ids
     * @return
     */
    @PostMapping("delDatasource")
    @CheckPermission("iot:screen:delDatasource")
    public Result<Boolean> delDatasource(@RequestBody List<String> ids) {
        return le5LeService.delDatasource(ids);
    }

    /**
     * 同步数据源
     * @param id
     * @return
     */
    @PostMapping("syncDatasource")
    @CheckPermission("iot:screen:syncDatasource")
    public Result<Boolean> syncDatasource(String id) {
        return le5LeService.syncDatasource(id);
    }
}
