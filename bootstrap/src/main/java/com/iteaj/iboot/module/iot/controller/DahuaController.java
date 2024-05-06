package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.hutool.http.Method;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.IClient;
import com.dahuatech.icc.oauth.model.v202010.GeneralRequest;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaICCProtocolType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 大华开放平台
 */
@RestController
@RequestMapping("/iot/dahua")
public class DahuaController extends BaseController {

    private final IClient client;

    public DahuaController(IClient client) {
        this.client = client;
    }

    /**
     * 监控设备列表
     * @return
     */
    @GetMapping("monitor")
    public Result<Page> monitor(Page page, Long isOnline) throws ClientException {
        doGetData(page, isOnline, null, new String[] {"1_2", "1_6"});
        return success(page);
    }

    /**
     * 门禁设备列表
     * @return
     */
    @GetMapping("access")
    public Result<Page> access(Page page, Long isOnline) throws ClientException {
        doGetData(page, isOnline, new String[] {"8"}, null);
        return success(page);
    }


    /**
     * 门禁设备列表
     * @param type 操作类型
     * @param data channelCodeList 通道编码列表 String[]
     * @return
     */
    @PostMapping("access/ctrl")
    public Result ctrlAccess(DahuaICCProtocolType type, @RequestBody JSONObject data) throws ClientException {
        if(type==null || data == null || data.isEmpty()) {
            return BooleanResult.buildTrue("参数错误");
        }

        GeneralRequest generalRequest = new GeneralRequest(type.getUrl(), Method.POST);
        generalRequest.body(data.toJSONString());
        // 发起请求处理应答
        GeneralResponse generalResponse = client.doAction(generalRequest, generalRequest.getResponseClass());
        JSONObject resp = JSONObject.parseObject(generalResponse.getResult());
        if(generalResponse.isSuccess()) {
            if(resp.getBooleanValue("success")) {
                return success("发送"+type.getDesc()+"指令成功");
            } else {
                return fail(resp.getString("errMsg"));
            }
        }

        return fail("发送"+type.getDesc()+"指令失败["+resp.getString("desc")+"]");
    }

    @PostMapping("getStream")
    public Result getStream(@RequestBody JSONObject json) throws ClientException {
        String url = json.getString("url");
        JSONObject data = json.getJSONObject("data");
        GeneralRequest generalRequest = new GeneralRequest(url, Method.POST);
        generalRequest.body(data.toJSONString());
        // 发起请求处理应答
        GeneralResponse generalResponse = client.doAction(generalRequest, generalRequest.getResponseClass());
        JSONObject resp = JSONObject.parseObject(generalResponse.getResult());
        if(generalResponse.isSuccess()) {
            JSONObject respData = resp.getJSONObject("data");
            return success(respData);
        }
        return fail("获取流失败["+resp.getString("desc")+"]");
    }

    private void doGetData(Page page, Long isOnline, String[] categorys, String[] types) throws ClientException {
        GeneralRequest generalRequest = new GeneralRequest("/evo-apigw/evo-brm/1.2.0/device/subsystem/page", Method.POST);
//        // set http post body
        JSONObject json = new JSONObject();
        if(categorys != null) {
            json.put("categorys", categorys);
        }
        if(types != null) {
            json.put("types", types);
        }

        json.put("unitTypes", new String[]{"1"});
        json.put("pageNum", page.getCurrent());
        json.put("pageSize", page.getSize());
        if (isOnline != null) {
            json.put("isOnline", isOnline);
        }
        generalRequest.body(json.toJSONString());
        // 发起请求处理应答
        GeneralResponse generalResponse = client.doAction(generalRequest, generalRequest.getResponseClass());
        if (generalResponse.isSuccess()) {
            String result = generalResponse.getResult();
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            page.setTotal(data.getInteger("totalRows"))
                    .setRecords(data.getJSONArray("pageData"));
        }
    }
}
