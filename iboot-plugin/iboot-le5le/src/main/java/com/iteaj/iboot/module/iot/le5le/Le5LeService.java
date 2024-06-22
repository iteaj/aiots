package com.iteaj.iboot.module.iot.le5le;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.Dataset;
import com.iteaj.iboot.module.iot.entity.Le5LeDatasource;
import com.iteaj.iboot.module.iot.mapper.Le5LeDatasourceMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
public class Le5LeService implements InitializingBean {

    private static String token;
    private final Le5LeDatasourceMapper le5LeDatasourceMapper;
    private static final String API_HOST = "https://v.le5le.com/api";
    private static final String ACCOUNT_HOST = "https://account.le5le.com/api";
    private static Logger logger = LoggerFactory.getLogger(Le5LeService.class);

    public String login(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", username);
        jsonObject.put("password", password);

        HttpRequest post = HttpUtil.createPost(ACCOUNT_HOST + "/account/login");
        HttpResponse execute = post.body(jsonObject.toJSONString()).execute();
        if(execute.isOk()) {
            JSONObject parseObject = JSONObject.parseObject(execute.body());
            if(!parseObject.containsKey("error")) {
                return token = parseObject.getString("token");
            } else {
                logger.error("<<< 登录乐吾乐失败 {}", parseObject.getString("error"));
            }
        } else {
            logger.error("<<< 登录乐吾乐失败 {}", execute.getStatus());
        }

        throw new ServiceException("登录乐吾乐失败");
    }

    /**
     * 获取大屏图纸列表
     * @param page
     * @return
     */
    public Page list(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = accountRequest("/data/v/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    /**
     * 获取大屏详情
     * @param id 大屏id
     * @return
     */
    public JSONObject get(String id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        return accountRequest("/data/v/get", jsonObject);
    }

    /**
     * 获取2d图纸列表
     * @param page
     * @return
     */
    public Page list2d(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = accountRequest("/data/2d/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    /**
     * 获取3d图纸列表
     * @param page
     * @return
     */
    public Page list3d(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = accountRequest("/data/3d/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    /**
     * 获取大屏组件列表
     * @param page
     * @return
     */
    public Page listComponents(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = accountRequest("/data/v.component/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    public Page pageDataSources(Page page, Le5LeDatasource entity) {
        return le5LeDatasourceMapper.detailPage(page, entity);
    }

    protected JSONObject apiRequest(String url, JSONObject body) {
        return this.doRequest(API_HOST + url, body, false);
    }

    protected JSONObject accountRequest(String url, JSONObject body) {
        return this.doRequest(ACCOUNT_HOST + url, body, false);
    }

    protected JSONObject doRequest(String url, JSONObject body, boolean retry) {
        if(token == null) {
            this.login("18059222381", "lcjg4OD12aXRlYXp");
        }

        HttpRequest post = HttpUtil.createPost(url);
        post.header("Authorization", token);
        HttpResponse execute = post.body(body.toString(SerializerFeature.SkipTransientField)).execute();
        if(execute.isOk()) {
            JSONObject response = JSONObject.parseObject(execute.body());
            String error = response.getString("error");
            if(error == null) {
                return response;
            } else if("没有访问权限".equals(error) && !response.containsKey("detail")) {
                if(!retry) {
                    this.login("18059222381", "lcjg4OD12aXRlYXp");
                    return doRequest(url, body, true);
                } else {
                    throw new ServiceException(error);
                }
            } else {
                logger.error("<<< 请求乐吾乐接口失败 {}", error);
            }
        }

        throw new ServiceException("请求接口失败");
    }

    public Page listDatasource(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = apiRequest("/data/datasource/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    public BooleanResult saveOrUpdate(Le5LeDatasource entity) {

        if(entity.getId() == null) {
            JSONObject data = new JSONObject();
            entity.setDeviceGroupId(null);
            JSONObject params = (JSONObject)JSONObject.toJSON(entity);

            if(entity.getType().equals("dataset")) {
                List<Dataset> devices = new ArrayList<>();
                devices.add(new Dataset("23438-temp", "integer", "温度", "23438"));
                data.put("devices", devices);
                data.put("mode", "");
                data.put("url", "http://iot.iteaj.com/api/iot/dataset");
                params.put("data", data);
                JSONObject response = this.apiRequest("/data/datasource/add", params);
                entity.setDatasourceId(response.getString("id"));
                entity.setCreatedAt(response.getDate("createdAt"));
                entity.setUpdateAt(response.getDate("updatedAt"));
                System.out.println(response);
            }

            return new BooleanResult(le5LeDatasourceMapper.insert(entity) > 0);
        } else {
            return new BooleanResult(le5LeDatasourceMapper.updateById(entity) > 0);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.login("18059222381", "lcjg4OD12aXRlYXp");
//        Page page = this.listComponents(new Page());
//        Page list = this.list(new Page());
//        if(list.getTotal() > 0) {
//            JSONObject o = (JSONObject)list.getRecords().get(0);
//            JSONObject id = this.get(o.getString("id"));
//            System.out.println(id);
//        }
//        Page list2d = this.list2d(new Page());
//        Page list3d = this.list3d(new Page());
//        System.out.println(page.getRecords());
    }

    public BooleanResult delDatasource(List<Long> ids) {
        return new BooleanResult(le5LeDatasourceMapper.deleteBatchIds(ids) > 0);
    }

    public BooleanResult syncDatasource(Long id) {
        Le5LeDatasource le5LeDatasource = le5LeDatasourceMapper.selectById(id);
        if(le5LeDatasource == null) {
            return BooleanResult.buildFalse("记录不存在");
        }

        if(le5LeDatasource.getType().equals("dataset")) {
            if(le5LeDatasource.getDeviceGroupId() == null) {
                return BooleanResult.buildFalse("未指定设备组");
            }

            List<Dataset> datasets = le5LeDatasourceMapper.listDatasetByDeviceGroupId(le5LeDatasource.getDeviceGroupId());
        }

        return null;
    }

}
