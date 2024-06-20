package com.iteaj.iboot.module.iot.le5le;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 获取大屏列表
     * @param page
     * @return
     */
    @GetMapping("list")
    public Result<Page> list(Page page) {
        return success(le5LeService.list(page));
    }

    /**
     * 获取大屏详情
     * @param id
     * @return
     */
    @GetMapping("get")
    public Result<JSONObject> get(String id) {
        return success(le5LeService.get(id));
    }
}
